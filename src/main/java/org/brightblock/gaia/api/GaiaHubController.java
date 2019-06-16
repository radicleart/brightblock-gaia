package org.brightblock.gaia.api;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
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
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.GroupGrantee;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.Permission;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;

@Controller
@CrossOrigin(origins = { "http://localhost:8080", "https://radicle.art", "https://dbid.io", "https://brightblock.org", "http://localhost:8888" }, maxAge = 6000)
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
	public ResponseEntity<StoreResponseModel> store(HttpServletRequest request, @PathVariable String address, @PathVariable String filename, @RequestBody String data) throws IOException {
		
		InputStream inputStream = new ByteArrayInputStream(data.getBytes(Charset.forName("UTF-8")));
        
		ObjectMetadata omd = new ObjectMetadata();
        omd.setContentType(MediaType.TEXT_PLAIN.toString());
		PutObjectRequest por = new PutObjectRequest(awsSettings.getBucket(), address + "/" + filename, inputStream, omd);
        
		AccessControlList acl = new AccessControlList();
        acl.grantPermission(GroupGrantee.AllUsers, Permission.Read);
		por.setAccessControlList(acl);
		s3.putObject(por);
		return new ResponseEntity<StoreResponseModel>(new StoreResponseModel(gaiaSettings.getReadUrlPrefix() + address + "/" + filename), HttpStatus.OK);

		
//		S3Object s3Object = s3.getObject(awsSettings.getBucket(), address + "/" + filename);
//		AccessControlList acl = s3.getObjectAcl(awsSettings.getBucket(), address + "/" + filename);
//		//acl.getGrantsAsList().clear();
//        acl.grantPermission(GroupGrantee.AllUsers, Permission.Read);
//		PutObjectRequest por = new PutObjectRequest(awsSettings.getBucket(), address + "/" + filename, inputStream, omd);
//		por.setAccessControlList(acl);
//		s3.putObject(por);
//		return new ResponseEntity<StoreResponseModel>(new StoreResponseModel(gaiaSettings.getReadUrlPrefix() + address + "/" + filename), HttpStatus.OK);
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
