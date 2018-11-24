package org.brightblock.gaia.api;

import javax.servlet.http.HttpServletRequest;

import org.brightblock.gaia.conf.settings.GaiaSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class GaiaController {

	@Autowired private GaiaSettings gaiaSettings;

	@RequestMapping(value = "/hub/{address}/{filename}", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<HubInfoModel> getFile(HttpServletRequest request, @PathVariable String address, @PathVariable String filename) {
		HubInfoModel model = new HubInfoModel(address, filename, null);
		return new ResponseEntity<HubInfoModel>(model, HttpStatus.OK);
	}

	@RequestMapping(value = "/hub_info", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<HubInfoModel> hubInfo(HttpServletRequest request) {
		HubInfoModel model = new HubInfoModel(gaiaSettings.getChallengeText(), gaiaSettings.getReadUrlPrefix(), gaiaSettings.getLatestAuthVersion());
		return new ResponseEntity<HubInfoModel>(model, HttpStatus.OK);
	}

	@RequestMapping(value = "/store/{address}/{filename}", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<HubInfoModel> store(HttpServletRequest request, @PathVariable String address, @PathVariable String filename, @RequestBody Object data) {
		HubInfoModel model = new HubInfoModel(address, filename, null);
		return new ResponseEntity<HubInfoModel>(model, HttpStatus.OK);
	}

	@RequestMapping(value = "/list-files/{address}", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<HubInfoModel> listFiles(HttpServletRequest request, @PathVariable String address, @RequestBody Object data) {
		HubInfoModel model = new HubInfoModel(address, null, null);
		return new ResponseEntity<HubInfoModel>(model, HttpStatus.OK);
	}

}
