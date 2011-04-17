package dk.tb.requesthandlers;

public interface ResourceRequestHandler {

	public boolean isResourceRequest(String path);

	public String resolveResource(String path);

}