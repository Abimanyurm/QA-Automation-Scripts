package config;

import java.util.Arrays;
import java.util.List;

public class ProxyManager {
	
    // List of proxies in format "IP:PORT"
    private static final List<String> proxies = Arrays.asList(
    		"192.0.2.1",
    		"192.0.2.45",
    		"198.51.100.10"
    		

    );

    // Use reference or round-robin logic
    public static String[] getProxy(int reference) {
        int index = reference % proxies.size();
        String[] parts = proxies.get(index).split(":");
        return new String[]{parts[0], parts[1]}; // {IP, PORT}
    }
	

}
