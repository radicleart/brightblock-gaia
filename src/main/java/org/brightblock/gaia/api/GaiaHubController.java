package org.brightblock.gaia.api;

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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestClientException;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;

@Controller
@CrossOrigin(origins = { "http://localhost:8080", "https://radicle.art", "https://brightblock.org", "http://localhost:8888" }, maxAge = 6000)
/**
 * @see https://github.com/blockstack/gaia
 * @author mike cohen
 *
 */
public class GaiaHubController {

	// https://hub.blockstack.org http://localhost:8195
	@Autowired private GaiaSettings gaiaSettings;
	@Autowired private AWSSettings awsSettings;
	final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
    private static final Logger logger = LogManager.getLogger(GaiaHubController.class);

	@RequestMapping(value = "/read/{address}/{filename:.+}", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getFile(HttpServletRequest request, HttpServletResponse response, @PathVariable String address, @PathVariable String filename) {
		try {
			String s3Object = s3.getObjectAsString(awsSettings.getBucket(), address + "/" + filename);
			return new ResponseEntity<String>(s3Object, HttpStatus.OK);
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
