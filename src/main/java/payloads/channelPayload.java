package payloads;

import java.util.List;



public class channelPayload {
	private String name;
	private boolean selected;

	public channelPayload() {
	}

	public channelPayload(String name, boolean selected) {
		this.name = name;
		this.selected = selected;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
    @Override
    public String toString() {
        return "{name='" + name + '\'' + ", selected=" + selected + '}';
    }

	// ---------- Nested wrapper class ----------

	public static class ChannelRequest {
		private List<channelPayload> channels;

		public ChannelRequest() {
		}

		public ChannelRequest(List<channelPayload> channels) {
			this.channels = channels;
		}

		public List<channelPayload> getChannels() {
			return channels;
		}

		public void setChannels(List<channelPayload> channels) {
			this.channels = channels;
		}
		
		 @Override  //--- for clear capturing payload in reports
	       public String toString() {
	            return "{channels=" + (channels != null ? channels.toString() : "[]") + "}";
		
	}
	
	
}}
