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
	private static final String PROTOCOL = "http://";
	private static final String COLON = ":";
	private String bitcoinBase;
	private String bitcoinHost;
	private String bitcoinPort;
	@Value("${spring.profiles.active}")
	private String activeProfile;

	public ApplicationSettings() {
		super();
        try {
            Process p = Runtime.getRuntime().exec("ip route show");
            
            BufferedReader stdInput = new BufferedReader(new 
                 InputStreamReader(p.getInputStream()));

            // read the output from the command
            String s = null;
            while ((s = stdInput.readLine()) != null) {
                System.out.println("Found: " + s);
            	if (s.indexOf("default") > -1) {
            		String[] parts = s.split(" ");
            		bitcoinBase = parts[2];
            	}
            }
            System.out.println("Found host IP=" + bitcoinBase);
            if (activeProfile != "staging" && activeProfile != "productions") {
            	bitcoinBase = null;
            }
        }
        catch (IOException e) {
            System.out.println("exception happened - here's what I know: ");
            e.printStackTrace();
        }
	}

	public String getBitcoinPort() {
		return bitcoinPort;
	}

	public void setBitcoinPort(String bitcoinPort) {
		this.bitcoinPort = bitcoinPort;
	}

	public String getBitcoinHost() {
		return bitcoinHost;
	}

	public void setBitcoinHost(String bitcoinHost) {
		this.bitcoinHost = bitcoinHost;
	}

	public String getBitcoinBase() {
		if (bitcoinBase != null) {
			return PROTOCOL + bitcoinBase + COLON + bitcoinPort;
		}
		return bitcoinHost + COLON + bitcoinPort;
	}
}
