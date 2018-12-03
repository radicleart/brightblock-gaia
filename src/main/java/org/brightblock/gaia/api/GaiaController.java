package org.brightblock.gaia.api;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.brightblock.gaia.conf.jwt.GaiaSettings;
import org.brightblock.gaia.conf.settings.AWSSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;

@Controller
public class GaiaController {

	private static final String _064X = "%064x";
	@Autowired private GaiaSettings gaiaSettings;
	@Autowired private AWSSettings awsSettings;
	@Autowired private RestTemplate restTemplate;
	private static final String CONFIG_SECRET = "d1294e7f0867bb593aa9c2e0bfcfd4d08483d1a6f594d4c35c67afafdeabce12";
	final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
    private static final Logger logger = LogManager.getLogger(GaiaController.class);

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

	@RequestMapping(value = "/read/{address}/{filename:.+}", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getFile(HttpServletRequest request, HttpServletResponse response, @PathVariable String address, @PathVariable String filename) {
		try {
			String s3Object = s3.getObjectAsString(awsSettings.getBucket(), address + "/" + filename);
			return new ResponseEntity<String>(s3Object, HttpStatus.OK);
//			ResponseEntity<String> fileObject = null;
//			String url = awsSettings.getReadUrl();
//			if (url.lastIndexOf("/") != url.length() - 1) {
//				url += "/";
//			}
//			url += address + "/" + filename;
//			HttpHeaders httpHeaders = new HttpHeaders();
//		    httpHeaders.setAccept(Collections.singletonList(MediaType.parseMediaType("text/plain")));
//		    HttpEntity<String> requestEntity = new HttpEntity<String>(httpHeaders);
//			fileObject = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
//			return fileObject;
		} catch (RestClientException e) {
			logger.warn("Failed to call local blockstack. Falling back to external node.");
			throw e;
		}
	}

	@RequestMapping(value = "/hub_info", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<HubInfoModel> hubInfo(HttpServletRequest request) {
		HubInfoModel model = new HubInfoModel(gaiaSettings.getChallengeText(), gaiaSettings.getReadUrlPrefix(), gaiaSettings.getLatestAuthVersion());
		return new ResponseEntity<HubInfoModel>(model, HttpStatus.OK);
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

	@RequestMapping(value = "/store/{address}/{filename:.+}", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<StoreResponseModel> store(HttpServletRequest request, @PathVariable String address, @PathVariable String filename, @RequestBody String data) {
		s3.putObject(awsSettings.getBucket(), address + "/" + filename, data);
		return new ResponseEntity<StoreResponseModel>(new StoreResponseModel(gaiaSettings.getReadUrlPrefix() + address + "/" + filename), HttpStatus.OK);
	}
	// "{"publicURL":"https://gaia.blockstack.org/hub/14FXKtccHBNfKpc7JoAyDf4mrGKR5CbyDT/profile.json"}"
	
	@RequestMapping(value = "/list-files/{address}", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<List<String>> listFiles(HttpServletRequest request, @PathVariable String address) {
		List<String> filenames = new ArrayList<>();
		ListObjectsV2Result result = s3.listObjectsV2(awsSettings.getBucket());
		List<S3ObjectSummary> objects = result.getObjectSummaries();
		for (S3ObjectSummary os: objects) {
			if (os.getKey().startsWith(address)) {
				filenames.add(os.getKey().substring(address.length() + 1));
			}
		    System.out.println("* " + os.getKey());
		}

		return new ResponseEntity<List<String>>(filenames, HttpStatus.OK);
	}

}
