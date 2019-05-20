package org.brightblock.gaia.api;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.servlet.http.HttpServletRequest;

import org.brightblock.gaia.conf.jwt.GaiaSettings;
import org.brightblock.gaia.conf.settings.AWSSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Controller
@CrossOrigin(origins = { "http://localhost:8080", "https://radicle.art", "https://brightblock.org", "http://localhost:8888" }, maxAge = 6000)
public class GaiaAdminController {

	private static final String _064X = "%064x";
	@Autowired private GaiaSettings gaiaSettings;
	private static final String CONFIG_SECRET = "d1294e7f0867bb593aa9c2e0bfcfd4d08483d1a6f594d4c35c67afafdeabce12";
	final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();

//	@RequestMapping(value = "/{address}/{filename}", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
//	public StreamingResponseBody getFile(HttpServletRequest request, HttpServletResponse response, @PathVariable String address, @PathVariable String filename) {
//        return new StreamingResponseBody() {
//            @Override
//            public void writeTo (OutputStream out) throws IOException {
//        	    S3Object o = null;
//        	    S3ObjectInputStream s3is = null;
//        	    OutputStream fos = null;
//        		try {
//        		    o = s3.getObject(awsSettings.getBucket(), filename);
//        		    s3is = o.getObjectContent();
//        		    fos = response.getOutputStream();
//        		    byte[] read_buf = new byte[1024];
//        		    int read_len = 0;
//        		    response.getOutputStream();
//        		    while ((read_len = s3is.read(read_buf)) > 0) {
//        		        fos.write(read_buf, 0, read_len);
//        		        fos.flush();
//                        try {
//                            Thread.sleep(5);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//        		    }
//        		} catch (AmazonServiceException e) {
//        			throw e;
//        		} catch (FileNotFoundException e) {
//        			throw e;
//        		} catch (IOException e) {
//        			throw e;
//        		} finally {
//        		    s3is.close();
//        		    fos.close();
//        		}
//            }
//        };
//	}

	@RequestMapping(value = "/config", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<GaiaSettings> config(HttpServletRequest request) {
		return new ResponseEntity<GaiaSettings>(gaiaSettings, HttpStatus.OK);
	}

	@RequestMapping(value = "/config", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> config(HttpServletRequest request, @RequestBody AWSSettings config) throws NoSuchAlgorithmException {
	    MessageDigest md = MessageDigest.getInstance("SHA-256");
	    // hint "gaia...";
	    md.update(config.getConfigSecret().getBytes(StandardCharsets.UTF_8));
	    byte[] digest = md.digest();
	    String hex = String.format(_064X, new BigInteger(1, digest));
	    
	   	if (!hex.equals(CONFIG_SECRET)) {
			return new ResponseEntity<String>("Wrong secret!", HttpStatus.FORBIDDEN);
	   	}
	   	gaiaSettings.addConfig(config);
		return new ResponseEntity<String>("Sucess.", HttpStatus.OK);
	}

	

}
