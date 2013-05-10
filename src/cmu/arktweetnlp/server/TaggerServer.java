package cmu.arktweetnlp.server;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import cmu.arktweetnlp.Tagger;
import cmu.arktweetnlp.Tagger.TaggedToken;
import cmu.arktweetnlp.Twokenize;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.ServerRunner;

public class TaggerServer extends NanoHTTPD {
	
	private Tagger tagger;
	
    public TaggerServer() {
        super(8080);
        
        String modelFile = "/home/mbone/voxsup/ark-tweet-nlp/model.20120919";
        
		tagger = new Tagger();
		try {
			tagger.loadModel(modelFile);
		} catch (IOException e) {
			System.err.println("Could not load up model file: " + modelFile);
		}        
    }
    
    /**
     * Return a response with a JSON error string.
     * @param error
     * @return
     */
    private Response errorResponse(String error) {
    	JSONObject errorObj = new JSONObject();
    	errorObj.put("error", error);
    	return new NanoHTTPD.Response(errorObj.toString());
    }
    
    private JSONObject tokenizeAndTag(String text) {
		JSONObject tokenObj = new JSONObject();
		tokenObj.put("original", text);
		
		List<TaggedToken> taggedTokens = tagger.tokenizeAndTag(text);
		for (TaggedToken taggedToken: taggedTokens) {
			tokenObj.append("tags", taggedToken.tag);
			tokenObj.append("tokens", taggedToken.token);
		}
		return tokenObj;
    }
    
    private JSONObject justTokenize(String text) {
		JSONObject tokenObj = new JSONObject();
		tokenObj.put("original", text);
		
		List<String> tokens = Twokenize.tokenize(text);
		
		for (String token: tokens) {
			tokenObj.append("tokens", token);
		}
		return tokenObj;    	
    }
    
    
	@Override
	public Response serve(String uri, 
			Method method,
			Map<String, String> header, 
			Map<String, String> params,
			Map<String, String> files) {
		
		if (this.tagger == null) {
			return this.errorResponse("Tagger has not been initialized. Restart the server.");
		}

		if (params.size() == 0) {
			return new NanoHTTPD.Response(new JSONObject().toString());
		}
		
		JSONObject jsonObj = new JSONObject();
		
		for (Map.Entry<String, String> entry: params.entrySet()) {
			if (!entry.getKey().equals("NanoHttpd.QUERY_STRING")) {
				
				if (uri.equals("/tokenize")) {
					jsonObj.put(entry.getKey(), this.justTokenize(entry.getValue()));	
				} else {
					jsonObj.put(entry.getKey(), this.tokenizeAndTag(entry.getValue()));
				}
			}
		}
		
		return new NanoHTTPD.Response(jsonObj.toString());
	}

	public static void main(String [] args) {
		ServerRunner.run(TaggerServer.class);
	}
}
