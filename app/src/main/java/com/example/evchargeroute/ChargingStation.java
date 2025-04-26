
// java/com/example/evchargeroute/ChargingStation.java
package com.example.evchargeroute;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ChargingStation {
    @SerializedName("ID")
    private int id;
    
    @SerializedName("AddressInfo")
    private AddressInfo addressInfo;
    
    @SerializedName("Connections")
    private List<Connection> connections;
    
    public int getId() {
        return id;
    }
    
    public AddressInfo getAddressInfo() {
        return addressInfo;
    }
    
    public List<Connection> getConnections() {
        return connections;
    }
    
    public String getConnectionTypesAsString() {
        if (connections == null || connections.isEmpty()) {
            return "Bilinmiyor";
        }
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < connections.size(); i++) {
            if (connections.get(i).getConnectionType() != null) {
                sb.append(connections.get(i).getConnectionType().getTitle());
                if (i < connections.size() - 1) {
                    sb.append(", ");
                }
            }
        }
        
        return sb.toString().isEmpty() ? "Bilinmiyor" : sb.toString();
    }
    
    public static class AddressInfo {
        @SerializedName("ID")
        private int id;
        
        @SerializedName("Title")
        private String title;
        
        @SerializedName("AddressLine1")
        private String addressLine1;
        
        @SerializedName("Town")
        private String town;
        
        @SerializedName("StateOrProvince")
        private String stateOrProvince;
        
        @SerializedName("Country")
        private Country country;
        
        @SerializedName("Latitude")
        private double latitude;
        
        @SerializedName("Longitude")
        private double longitude;
        
        public int getId() {
            return id;
        }
        
        public String getTitle() {
            return title;
        }
        
        public String getAddressLine1() {
            return addressLine1;
        }
        
        public String getTown() {
            return town;
        }
        
        public String getStateOrProvince() {
            return stateOrProvince;
        }
        
        public Country getCountry() {
            return country;
        }
        
        public double getLatitude() {
            return latitude;
        }
        
        public double getLongitude() {
            return longitude;
        }
        
        public String getFullAddress() {
            StringBuilder sb = new StringBuilder();
            
            if (title != null && !title.isEmpty()) {
                sb.append(title).append(", ");
            }
            
            if (addressLine1 != null && !addressLine1.isEmpty()) {
                sb.append(addressLine1).append(", ");
            }
            
            if (town != null && !town.isEmpty()) {
                sb.append(town).append(", ");
            }
            
            if (stateOrProvince != null && !stateOrProvince.isEmpty()) {
                sb.append(stateOrProvince).append(", ");
            }
            
            if (country != null && country.getTitle() != null) {
                sb.append(country.getTitle());
            }
            
            String address = sb.toString();
            if (address.endsWith(", ")) {
                address = address.substring(0, address.length() - 2);
            }
            
            return address;
        }
    }
    
    public static class Country {
        @SerializedName("ID")
        private int id;
        
        @SerializedName("Title")
        private String title;
        
        public int getId() {
            return id;
        }
        
        public String getTitle() {
            return title;
        }
    }
    
    public static class Connection {
        @SerializedName("ID")
        private int id;
        
        @SerializedName("ConnectionType")
        private ConnectionType connectionType;
        
        @SerializedName("PowerKW")
        private double powerKW;
        
        public int getId() {
            return id;
        }
        
        public ConnectionType getConnectionType() {
            return connectionType;
        }
        
        public double getPowerKW() {
            return powerKW;
        }
    }
    
    public static class ConnectionType {
        @SerializedName("ID")
        private int id;
        
        @SerializedName("Title")
        private String title;
        
        public int getId() {
            return id;
        }
        
        public String getTitle() {
            return title;
        }
    }
}
