package org.brightblock.gaia.conf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "application")
public class ApplicationSettings {
	private String blockstackBase;
	private String blockstackOrgBase;
	private String mongoIp;
	private String containerHostIp;
	@Value("${spring.profiles.active}")
	private String activeProfile;

	public ApplicationSettings() {
		super();
        try {
    		Process p = Runtime.getRuntime().exec("ip route show");
            
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

            // read the output from the command
            String s = null;
            while ((s = stdInput.readLine()) != null) {
                System.out.println("Found: " + s);
            	if (s.indexOf("default") > -1) {
            		String[] parts = s.split(" ");
            		containerHostIp = parts[2];
            	}
            }
            System.out.println("Found host IP=" + containerHostIp);
        }
        catch (IOException e) {
            System.out.println("exception happened - here's what I know: ");
            e.printStackTrace();
        }
	}

	public String getBlockstackBase() {
		return blockstackBase;
	}

	public void setBlockstackBase(String blockstackBase) {
		this.blockstackBase = blockstackBase;
	}

	public String getBlockstackOrgBase() {
		return blockstackOrgBase;
	}

	public void setBlockstackOrgBase(String blockstackOrgBase) {
		this.blockstackOrgBase = blockstackOrgBase;
	}

	public String getMongoIp() {
		if (activeProfile.equals("staging") || activeProfile.equals("production")) {
        	return containerHostIp;
		} else {
    		return mongoIp;
        }
	}

	public void setMongoIp(String mongoIp) {
		this.mongoIp = mongoIp;
	}
}
