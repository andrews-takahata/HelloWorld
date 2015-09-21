package poc;

import com.ca.www.UnicenterServicePlus.ServiceDesk.ArrayOfString;
import com.ca.www.UnicenterServicePlus.ServiceDesk.USD_WebServiceSoap;
import javax.xml.rpc.holders.StringHolder;

class Main {
	public static void main(String[] args) throws Exception {
		z_functions.carregaConfigs();
		USD_WebServiceSoap usd;
		int sid;
		usd = z_functions.getSoap();
		sid = z_functions.getSession(usd);
		
		try {
		    String userHandle_USD = usd.getHandleForUserid(sid, "control-m");
		    //String resumo = "Falha na execução de job";
		    //String descricao = "Falha na execução do JOB 2131231 programado para a data 232323";
		    String resumo = args[0];
		    String descricao = args[1];
		    String categoria = z_functions.getRequestAreaPersidSym(sid, "Control-M.Falha em deploy");
		    
		    //System.out.println( "Argumento 1: " + args[0] );
		    //System.out.println( "Argumento 2: " + args[1] );
		    
			ArrayOfString attrVal = new ArrayOfString();
			attrVal.setString(new String[] { "customer", userHandle_USD,
											 "requested_by", userHandle_USD,
											 "description", descricao,
											 "category", categoria,
											 "summary", resumo
										   });
			
			ArrayOfString attr = new ArrayOfString();
			attr.setString(new String[0]);
			
			ArrayOfString prop = new ArrayOfString();
			prop.setString(new String[0]);
			
			StringHolder issuePersid = new StringHolder();
			StringHolder issueNumber = new StringHolder();
			
			usd.createRequest(sid, userHandle_USD, attrVal, prop, "", attr, issuePersid, issueNumber);
			System.out.println("Request criada: " + issueNumber.value);
			
			usd.updateObject(sid, issuePersid.value, attrVal, prop);
			//System.out.println( "Update feito na Request " + issueNumber.value );
	    }
		
	    catch (Exception ex) {
	    	z_functions.print_error(ex);
        }
		
		finally {
			usd.logout(sid);
			//System.out.println("Feito logoff no SDM. Tchau!");
		}
	}
}