package org.brightblock.gaia.api.dropbox;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.dropbox.core.DbxApiException;
import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.users.FullAccount;

@Controller
@CrossOrigin(origins = { "http://localhost:8080", "http://localhost:8081", "https://loopbomb.com", "https://test.loopbomb.com", "https://radicle.art", "https://tart.radiclesociety.org", "https://tdbid.radiclesociety.org",  "https://dbid.io", "https://brightblock.org", "http://localhost:8888" }, maxAge = 6000)
public class DropboxController {

	@Autowired private DbxClientV2 client;

	@RequestMapping(value = "/currentAccount", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<FullAccount> config(HttpServletRequest request) throws DbxApiException, DbxException {
		FullAccount account = client.users().getCurrentAccount();
		System.out.println(account.getName().getDisplayName());
		return new ResponseEntity<FullAccount>(account, HttpStatus.OK);
	}
}
