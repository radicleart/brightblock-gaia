package org.brightblock.gaia.conf.jwt;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Arrays;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.Base58;
import org.bitcoinj.params.MainNetParams;
import org.bouncycastle.util.encoders.Hex;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

public class V1Authentication {

	private static final Logger logger = LogManager.getLogger(V1Authentication.class);
	private String token;
	private SignedJWT jwt;
	private JWTClaimsSet claims;

	public V1Authentication() {
		super();
	}

	public V1Authentication(String token) {
		try {
			this.token = token;
			this.jwt = SignedJWT.parse(token);
			this.claims = jwt.getJWTClaimsSet();
		} catch (Exception e) {
			logger.error("Error token: " + token, e);
			throw new RuntimeException("Failed to decode authentication token: " + token);
		}
	}

	/**
	 * "{"url_prefix":"https://brightblock.s3.amazonaws.com/",
	 * "address":"1EELUEFaFakdrZuGf9Yf2YLLrjj3sHYBKb",
	 * "token":"v1:eyJ0eXAiOiJKV1QiLCJhbGciOiJFUzI1NksifQ.eyJnYWlhQ2hhbGxlbmdlIjoiW1wiZ2FpYWh1YlwiLFwiMjAxOFwiLFwiXCIsXCJibG9ja3N0YWNrX3N0b3JhZ2VfcGxlYXNlX3NpZ25cIl0iLCJodWJVcmwiOiJodHRwczovL2dhaWEuYnJpZ2h0YmxvY2sub3JnIiwiaXNzIjoiMDIyNmVlZjk2MDI4YWYwMTQ1M2YwYzk2NGE0MTcxMGEzZDgwNGQ3MGY2MTgyOTZkMGVjMzczY2MxMGFhYjEwNjM4Iiwic2FsdCI6ImRmODk3YWRkMjVjZDBiNjE1MjUxZjViMmY1OGI3ODllIn0.FoeOdvMqWFU9tqVtToUHE7axjsA0YK_YArhFCXQ0eytRvJbkeW2S1h2V_iQF2311wq322CaPoIRZIxC6Rgqccg"
	 * "server":"https://gaia.brightblock.org"}"
	 * 
	 * @param authPart
	 * @return
	 */
	public static V1Authentication getInstance(String v1Token) {
		if (!v1Token.startsWith("v1:")) {
			throw new RuntimeException("Authorization header should start with v1:");
		}
		String token = v1Token.substring(3);
		return new V1Authentication(token);
	}

	/*
	 * Determine if the authentication token is valid: * must have signed the given
	 * `challengeText` * must not be expired * if it contains an associationToken,
	 * then the associationToken must authorize the given address.
	 *
	 * Returns the address that signed off on this token, which will be checked
	 * against the server's whitelist. * If this token has an associationToken, then
	 * the signing address is the address that signed the associationToken. *
	 * Otherwise, the signing address is the given address.
	 *
	 * this throws a ValidationError if the authentication is invalid
	 */
	public boolean isAuthenticationValid(String address, String challengeText, boolean requireCorrectHubUrl, String[] validHubUrls) {
		logger.info("===========================================================================");
		String issuer = getIssuer();
		checkKeysMatch(address, issuer);
		logger.info("keys match: check");
		checkHubUrls(requireCorrectHubUrl, validHubUrls);
		logger.info("hub urls: check");
		checkExpiration();
		logger.info("Expiry: check");
		checkTokenAndChallengeText(challengeText, issuer);
		logger.info("Expiry: check");
		return true;
	}
	
	private String getIssuer() {
		String issuer = claims.getIssuer();
		if (issuer == null) {
			throw new RuntimeException("Must provide `iss` claim in JWT.");
		}
		if (issuer.startsWith("did:btc-addr:")) {
			issuer = issuer.substring(13);
		}
		return issuer;
	}

	private void checkKeysMatch(String address, String issuer) {
		String issuerAddressB58 = issuerAddressToB58(issuer);
		if (!issuerAddressB58.equals(address)) {
			throw new RuntimeException("Issuer Address not allowed to write on this path");
		}
//		String bitcoinAddressHex = bitcoinAddressToHex(address);
//		if (!issuer.equals(bitcoinAddressHex)) {
//			throw new RuntimeException("Issuer Address not allowed to write on this path");
//		}
	}

	public String issuerAddressToB58(String iss) {
		logger.info("Issuer: " + iss);
		logger.info("Issuer hex: " + new String(Hex.encode(iss.getBytes())), "UTF-8");
		org.bitcoinj.core.ECKey key = org.bitcoinj.core.ECKey.fromPublicOnly(Hex.decode(iss.getBytes()));
		Address issuerAddress = new Address(MainNetParams.get(), key.getPubKeyHash());
		logger.info("Issuer getPublicKeyAsHex: " + key.getPublicKeyAsHex());
		return issuerAddress.toBase58();
	}

	public String bitcoinAddressToHex(String address) {
		logger.info("address: " + address);
		logger.info("address Base58 decoded and hex: " + new String(Hex.encode(Base58.decode(address))), "UTF-8");
		logger.info("address hexed: " + new String(Hex.encode(Base58.decode(address))), "UTF-8");
		String hexString = new String(Hex.encode(Base58.decode(address)));
		return hexString;
	}

	private void checkHubUrls(boolean requireCorrectHubUrl, String[] validHubUrls) {
		if (requireCorrectHubUrl) {
			String hubUrl = this.claims.getClaim("hubUrl").toString();
			if (this.claims.getClaim("hubUrl") == null) {
				throw new RuntimeException(
						"Authentication must provide a claimed hub. You may need to update blockstack.js.");
			}
			if (hubUrl.endsWith("/")) {
				hubUrl = hubUrl.substring(0, hubUrl.length() - 2);
			}
			if (validHubUrls == null) {
				throw new RuntimeException("Configuration error on the gaia hub. validHubUrls must be supplied.");
			}
			boolean contains = Arrays.stream(validHubUrls).anyMatch(hubUrl::equals);
			if (!contains) {
				throw new RuntimeException(
						"Auth token's claimed hub url " + hubUrl + " not found in this hubs set: " + validHubUrls);
			}
		}
	}

	/**
	 * That the JWT is signed correctly by verifying with the pubkey hex provided as
	 * iss
	 * 
	 * @param challengeText
	 * @throws NoSuchAlgorithmException
	 * @throws SignatureException 
	 */
	private void checkTokenAndChallengeText(String challengeText, String issuer) {
		try {
			String gaiaChallenge = this.claims.getClaim("gaiaChallenge").toString();
			if (!challengeText.equals(gaiaChallenge)) {
				throw new RuntimeException("Invalid gaiaChallenge text in supplied JWT: " + gaiaChallenge);
			}
			org.bitcoinj.core.ECKey key = org.bitcoinj.core.ECKey.fromPublicOnly(Hex.decode(issuer.getBytes()));
			org.spongycastle.math.ec.ECPoint spoint = key.getPubKeyPoint();
			BigInteger xbg = spoint.getAffineXCoord().toBigInteger();
			BigInteger ybg = spoint.getAffineYCoord().toBigInteger();
			ECKey ecKey = new ECKey.Builder(Curve.P_256K, Base64URL.encode(xbg), Base64URL.encode(ybg))
			        .keyUse(KeyUse.SIGNATURE)
			        .keyID("1")
			        .build();
			ECDSAVerifier verifier = new ECDSAVerifier(ecKey);
			if (!jwt.verify(verifier)) {
				throw new RuntimeException("Verification of token failed.");
			}
		} catch (JOSEException e) {
			throw new RuntimeException("Unable to verify jwt.");
		}
	}

	private void checkExpiration()  {
		Date expirationTime = this.claims.getExpirationTime();
		if (expirationTime == null) {
			return;
			//throw new RuntimeException("No expiry set on token");
		}
		long expiry = expirationTime.getTime();
		long nowish = new Date().getTime();
		if (nowish < expiry) {
			throw new RuntimeException("Token has expired.");
		}
	}

	public String getToken() {
		return this.token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public SignedJWT getJwt() {
		return jwt;
	}

	public void setJwt(SignedJWT jwt) {
		this.jwt = jwt;
	}
}
