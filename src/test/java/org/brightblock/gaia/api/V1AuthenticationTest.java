package org.brightblock.gaia.api;

import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.Base64;
import java.util.Base64.Decoder;

import org.bouncycastle.util.encoders.Hex;
import org.brightblock.gaia.conf.jwt.V1Authentication;
import org.junit.Test;

import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

public class V1AuthenticationTest {

	private String challengeText = "[\"gaiahub\",\"2018\",\"\",\"blockstack_storage_please_sign\"]";
	private String v1Token1 = "v1:eyJ0eXAiOiJKV1QiLCJhbGciOiJFUzI1NksifQ.eyJnYWlhQ2hhbGxlbmdlIjoiW1wiZ2FpYWh1YlwiLFwiMjAxOFwiLFwiXCIsXCJibG9ja3N0YWNrX3N0b3JhZ2VfcGxlYXNlX3NpZ25cIl0iLCJodWJVcmwiOiJodHRwczovL2dhaWEuYnJpZ2h0YmxvY2sub3JnIiwiaXNzIjoiMDJhNTNlNmUzYjg4YzYzOTk3MWM3OTNjYTk0MzY4MmI2NDFjZjZkZmZhNmRlYmQwMTNiNTlmNmMyNDcwNzUxYTJkIiwic2FsdCI6IjIwNzY3MmViMTRhNzhhMjI4MDNkNDhhODdjNzk2ZmFlIn0.6J_QFKizycS9WbJfR0JdFDU03AdTSyha9LsCDKOAn_TO2b5fK6wQoL1R9Ncryz8O5-GmC7J1ojBJ_8C-TefL6A";
	private String address1_58 = "14SPEMDG1iwSZeZrb4ouwfXkEWxPNHp7wG";
	private String salt1 = "207672eb14a78a22803d48a87c796fae";
	
	private String address2_58 = "1EELUEFaFakdrZuGf9Yf2YLLrjj3sHYBKb";
	private String v1Token2 = "v1:eyJ0eXAiOiJKV1QiLCJhbGciOiJFUzI1NksifQ.eyJnYWlhQ2hhbGxlbmdlIjoiW1wiZ2FpYWh1YlwiLFwiMjAxOFwiLFwiXCIsXCJibG9ja3N0YWNrX3N0b3JhZ2VfcGxlYXNlX3NpZ25cIl0iLCJodWJVcmwiOiJodHRwczovL2dhaWEuYnJpZ2h0YmxvY2sub3JnIiwiaXNzIjoiMDIyNmVlZjk2MDI4YWYwMTQ1M2YwYzk2NGE0MTcxMGEzZDgwNGQ3MGY2MTgyOTZkMGVjMzczY2MxMGFhYjEwNjM4Iiwic2FsdCI6ImRmODk3YWRkMjVjZDBiNjE1MjUxZjViMmY1OGI3ODllIn0.FoeOdvMqWFU9tqVtToUHE7axjsA0YK_YArhFCXQ0eytRvJbkeW2S1h2V_iQF2311wq322CaPoIRZIxC6Rgqccg";
	private String salt2 = "df897add25cd0b615251f5b2f58b789e";
	
//	private String address1_Hex = "0025B5554BC668FA21312878BDA23CBDF55D61CE3A1FB7359B";
	// iss: 02a53e6e3b88c639971c793ca943682b641cf6dffa6debd013b59f6c2470751a2d
//	private String address = address1_58;
//	private String address2_Hex = "00911F4B838DB04D86C83172DEC634D3AE269736790066DA36";
//	private String addressHex = address1_Hex;
//	private String useToken = v1Token1;
	
	@Test
	public void testToken1() throws Exception {
		testToken(v1Token1, salt1, challengeText);
	}
	
	@Test
	public void testToken2() throws Exception {
		testToken(v1Token2, salt2, challengeText);
	}
	
	private void testToken(String thisToken, String salt, String challengeText) throws Exception {
		V1Authentication v1Authentication = V1Authentication.getInstance(thisToken);
		String token = v1Authentication.getToken();
		String[] tokens = token.split("\\.");
		
		Decoder d = Base64.getDecoder();
		String header = new String(d.decode(tokens[0]));
		String body = new String(d.decode(tokens[1]));
		String signingPart = tokens[0] + "." + tokens[1];
		SignedJWT jwt = SignedJWT.parse(token);
		JWTClaimsSet claims = jwt.getJWTClaimsSet();
		
		org.bitcoinj.core.ECKey key = org.bitcoinj.core.ECKey.fromPublicOnly(Hex.decode(claims.getIssuer().getBytes()));
		org.spongycastle.math.ec.ECPoint spoint = key.getPubKeyPoint();
		BigInteger xbg = spoint.getAffineXCoord().toBigInteger();
		BigInteger ybg = spoint.getAffineYCoord().toBigInteger();
		ECKey ecKey = new ECKey.Builder(Curve.P_256K, Base64URL.encode(xbg), Base64URL.encode(ybg))
		        .keyUse(KeyUse.SIGNATURE)
		        .keyID("1")
		        .build();

		ECDSAVerifier verifier = new ECDSAVerifier(ecKey);

		assertTrue(tokens.length == 3);
		assertTrue(header.indexOf("alg") > -1);
		assertTrue(body.indexOf("gaiaChallenge") > -1);
		assertTrue(token.startsWith(signingPart));
		assertTrue(claims.getClaim("gaiaChallenge").toString().equals(challengeText));
		assertTrue(claims.getClaim("salt").toString().equals(salt));
		assertTrue(jwt.verify(verifier));
	}
	
	@Test
	public void testAddress1() throws Exception {
		V1Authentication v1Authentication = V1Authentication.getInstance(v1Token1);
		boolean auth = v1Authentication.isAuthenticationValid(address1_58, challengeText, false, null, null);
		assertTrue(auth);
	}

	@Test
	public void testAddress2() throws Exception {
		V1Authentication v1Authentication = V1Authentication.getInstance(v1Token2);
		boolean auth = v1Authentication.isAuthenticationValid(address2_58, challengeText, false, null, null);
		assertTrue(auth);
	}

}


//KeyFactory fact = KeyFactory.getInstance("ECDSA", "BC");
//ECPoint point = new ECPoint(new BigInteger(1, x), new BigInteger(1, y));
//ECParameterSpec params = ECNamedCurveTable.getParameterSpec("prime239v1");

//KeyPair kp = new KeyPair(publicKey, privateKey);
//
//PublicKey pk = fact.generatePublic(new X509EncodedKeySpec(pair.getPublic().getEncoded()));
//
//ECNamedCurveParameterSpec parameterSpec = ECNamedCurveTable.getParameterSpec(name);
//ECParameterSpec spec = new ECNamedCurveSpec(name, parameterSpec.getCurve(), parameterSpec.getG(), parameterSpec.getN(), parameterSpec.getH(), parameterSpec.getSeed());
//ECPublicKey ecPublicKey = (ECPublicKey) eckf.generatePublic(new ECPublicKeySpec(point, spec));
//System.out.println(ecPublicKey.getClass().getName());
//
//ECPoint mypoint = new ECPoint(x, y);
//ECDSAVerifier jwsV = new ECDSAVerifier(ecJWK);
//jwt.verify(jwsV);





//ECDSASignature sig = ECDSASignature.decodeFromDER(sigi.decodeToString().getBytes());
//boolean valid = org.bitcoinj.core.ECKey.verify(signingPart.getBytes(), sigi, key.getPubKeyHash());
//assertTrue(valid);
//ECGenParameterSpec ecGenSpec = new ECGenParameterSpec("secp256r1");
//KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("ECDSA", "BC");
//keyPairGenerator.initialize(ecGenSpec, new SecureRandom());

//X509EncodedKeySpec ks = new X509EncodedKeySpec(claims.getIssuer().getBytes());
//KeyFactory kf;
//try {
//   kf = java.security.KeyFactory.getInstance("ECDSA");
//} catch (NoSuchAlgorithmException e) {
//  logger.error("Cryptography error: could not initialize ECDH keyfactory!", e);
//  return;
//}
//

//KeyPair pair = keyPairGenerator.generateKeyPair();

//KeyFactory keyFactory = KeyFactory.getInstance("EC", new BouncyCastleProvider());
//KeySpec publicKeySpec = new X509EncodedKeySpec( key.getPubKeyHash() );
//ECPublicKey remotePublicKey = (ECPublicKey)keyFactory.generatePublic( publicKeySpec );

//ASN1ObjectIdentifier usage = ECNamedCurveTable.getOID("secp256r1");
//ECPublicKeySpec spec = new ECPublicKeySpec(w, params);
//ECPublicKey bobPubKey = keyFactory.generatePublic(bobPubKeySpec);


//try {
//  remotePublicKey = (ECPublicKey) keyFactory.generatePublic(ks);
//} catch (InvalidKeySpecException e) {
//	logger.warn("Received invalid key specification from client", e);
//  throw e;
//} catch (ClassCastException e) {
//	logger.warn("Received valid X.509 key from client but it was not EC Public Key material",e);
//  throw e;
//}

//ECNamedCurveSpec params = new ECNamedCurveSpec("prime256v1", spec.getCurve(), spec.getG(), spec.getN());
//ECPoint point =  ECPointUtil.decodePoint(params.getCurve(), address.getBytes());
//ECPublicKeySpec pubKeySpec = new ECPublicKeySpec(point, params);
//ECPublicKey pk1 = (ECPublicKey) kf.generatePublic(pubKeySpec);

//ECDSAPublicKey ecdsaPublicKey = new ECDSAPublicKey(usage, address.getBytes());
//ECPublicKey pk2 = (ECPublicKey) ecdsaPublicKey.;
//
//ECPublicKey
//ECPublicKey pubkey = (ECPublicKey) ecdsaPublicKey;
//ecdsaPublicKey.getPublicPointY();

