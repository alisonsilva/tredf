package br.jus.tredf.justicanumeros.dao;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.PagedResultsControl;
import javax.naming.ldap.PagedResultsResponseControl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;

import br.jus.tredf.justicanumeros.dao.util.AutenticacaoCorporativa;
import br.jus.tredf.justicanumeros.model.exception.ParametroException;
import br.jus.tredf.justicanumeros.model.wrapper.UsuarioVO;
import br.jus.tredf.justicanumeros.util.PropertiesServiceController;

import com.unboundid.util.ssl.SSLUtil;
import com.unboundid.util.ssl.TrustAllTrustManager;

@Repository("ActiveDirectoryDao")
@SuppressWarnings("all")
public class ActiveDirectoryDao extends AutenticacaoCorporativa implements Serializable {
	
	private static final String USUARIO_ATIVO = "512";
	private static final String USUARIO_ATIVO_PASSWORD_NOT_REQUIRED = "544";
	private static final String USUARIO_ACCOUNT_DISABLED = "2";
	
	private static ActiveDirectoryDao AD_SERVICE;

	@Autowired private PropertiesServiceController propertiesServiceController;

	private InitialLdapContext ADContext;
	
	@Value("${ldap.activedirectory.host}")
	private String host;
	
	@Value("${ldap.activedirectory.dominio}")
	private String dominio;
	
	@Value("${ldap.activedirectory.porta}")
	private String porta;
	
	@Value("${ldap.activedirectory.sldapjks}")
	private String jks;
	
	@Value("${ldap.activedirectory.sldapporta}")
	private String portS;
	
	@Value("${ldap.activedirectory.senha}")
	private String senhaConsultaAD;
	
	@Value("${ldap.activedirectory.usuario}")
	private String usuarioConsultaAD;
	
	@Value("${ldap.activedirectory.ativo}")
	private Boolean flagAutenticaAd;
	
	public static ActiveDirectoryDao getInstance() {
		if(AD_SERVICE == null) {
			AD_SERVICE = new ActiveDirectoryDao();
		}
		return AD_SERVICE;
	}
	
	
	public ActiveDirectoryDao() {
		SSLUtil sslUtil = new SSLUtil(new TrustAllTrustManager());
		try {
			if (propertiesServiceController == null) {
				propertiesServiceController = PropertiesServiceController.getInstance();
			}
		} catch(Exception e) {	
			e.printStackTrace();
		}		
		AD_SERVICE = this;
	}
	
	@Override
	public boolean autenticacaoCorporativa() {
		return flagAutenticaAd;
	}
	
	/**
	 * Realiza o login do usuário no Active Directory
	 * @param login Login do usuário
	 * @param senha Senha do usuário
	 * @return InitialLdapContext contexto de login do usuário
	 */
	@Override
	public InitialLdapContext loginDominio(String login, String senha) {
		if (!flagAutenticaAd) {
			throw new ParametroException("Não é necessário autenticar Active Directory");
		}
		InitialLdapContext context = null;
		Hashtable<String, String> ldapEnv = new Hashtable<String, String>();
		String urlDC = "ldap://" + host + ":" + porta + "/";
		String userName = login + "@" + dominio;
		ldapEnv.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		ldapEnv.put(Context.SECURITY_AUTHENTICATION, "simple");
		ldapEnv.put(Context.SECURITY_PRINCIPAL, userName);
		ldapEnv.put(Context.SECURITY_CREDENTIALS, senha);
		ldapEnv.put(Context.PROVIDER_URL, urlDC);
		try {
			context= new InitialLdapContext(ldapEnv, null);
		} catch (NamingException e) {
			e.printStackTrace();
		}
		return context;
	}
	
	
	@Override
	public List<UsuarioVO> getTodosUsuarios() {
		List<UsuarioVO> lstUsuarios = new ArrayList<UsuarioVO>();
		String diretorioPesquisa = montaDCList();
		InitialLdapContext ctx = criaInitialContext(usuarioConsultaAD, senhaConsultaAD);
		String searchFilter = "(&(objectClass=person)(userPrincipalName=*@*))";
		String objAttribs[] = { "userPrincipalName", "name", "mail", "displayName", "userAccountControl", "distinguishedName", "canonicalName" };
		try {
			lstUsuarios = consultaLDAPTotal(ctx, diretorioPesquisa, searchFilter, objAttribs, 500);
		} catch (NamingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return lstUsuarios;
	}


	public void materializaAllElements(List<UsuarioVO> lstUsuarios, NamingEnumeration dirObjects) throws NamingException {
		while(dirObjects != null && dirObjects.hasMoreElements()) {
			UsuarioVO vo = new UsuarioVO();
			Object next = dirObjects.next();
			SearchResult dirObject = (SearchResult)next;
			Attributes atributos = dirObject.getAttributes();
			
			if (atributos.get("userPrincipalName") != null) {
				vo.lgn = ((String) atributos.get("userPrincipalName").get());
			} else {
				vo.lgn = "";
			}
			if (atributos.get("name") != null) {
				vo.cn = ((String) atributos.get("name").get());
			} else {
				vo.cn = "";
			}
			if (atributos.get("distinguishedName") != null) {
				vo.distinguishedName = ((String) atributos.get("distinguishedName").get());
			} else {
				vo.distinguishedName = "";
			}
			if (atributos.get("canonicalName") != null) {
				vo.canonicalName = ((String) atributos.get("canonicalName").get());
			} else {
				vo.canonicalName = "";
			}
			if (atributos.get("displayName") != null) {
				vo.showName = ((String) atributos.get("displayName").get());
			} else {
				vo.showName = "";
			}
			if (atributos.get("userAccountControl") == null) {
				vo.ativo = true;					
			} else {
				String userAccountControl = (String) atributos.get("userAccountControl").get();

				if (!USUARIO_ATIVO.equals(userAccountControl) 
						|| USUARIO_ACCOUNT_DISABLED.equals(userAccountControl)) {
					vo.ativo = false;
				} else {
					vo.ativo = true;
				}
			}
			Attribute atributoEmail = atributos.get("mail");
			if (atributoEmail != null) {
				vo.email = ((String) atributoEmail.get());
			}
			vo.sn = "";
			lstUsuarios.add(vo);
		}
	}


	@Override
	public UsuarioVO getDadosUsuario(String login, String senha) {
		UsuarioVO vo = null;
		String diretorioPesquisa = montaDCList();
		InitialLdapContext ctx = criaInitialContext(usuarioConsultaAD, senhaConsultaAD);
		String searchFilter = "(&(objectClass=person)(userPrincipalName=" + login + "@*))";
		String objAttribs[] = { "userPrincipalName", "name", "mail", "displayName", "userAccountControl" };
		try {
			NamingEnumeration dirObjects = consultaLDAP(ctx, diretorioPesquisa,	searchFilter, objAttribs);
			if (dirObjects != null && dirObjects.hasMoreElements()) {
				vo = new UsuarioVO();
				
				Object next = dirObjects.next();
				SearchResult dirObject = (SearchResult) next;
				Attributes atributos = dirObject.getAttributes();
				boolean contaHabilitada = verificaContaAtiva(dirObject);
				vo.ativo = contaHabilitada ? true : false;

				vo.lgn = login.toLowerCase();
				vo.cn = ((String) atributos.get("name").get());
				vo.showName = ((String) atributos.get("displayName").get());
				Attribute atributoEmail = atributos.get("mail");
				if (atributoEmail != null) {
					vo.email = ((String) atributoEmail.get());
				} else {
				  vo.email = vo.cn + "@tre-df.gov.br";
				}
				vo.sn = senha;
			}
			ctx.close();
		} catch (NamingException e) {
			e.printStackTrace();
		}

		return vo;
	}

	@Override
	public UsuarioVO getDadosUsuarioPorEmail(String email) {
		UsuarioVO vo = null;
		try {
			InitialLdapContext ctx = criaInitialContext(usuarioConsultaAD, senhaConsultaAD);
			String dcList = montaDCList();
			String searchBase = dcList;
			String searchFilter = "(&(objectClass=user)(mail=" + email + "))";
			String objAttribs[] = { "userPrincipalName" };
			NamingEnumeration dirObjects = consultaLDAP(ctx, searchBase, searchFilter, objAttribs);
			if (dirObjects != null && dirObjects.hasMoreElements()) {
				while (dirObjects.hasMoreElements()) {
					SearchResult dirObject = (SearchResult) dirObjects.next();
					Attributes atributos = dirObject.getAttributes();
					Attribute attrLogin = atributos.get("userPrincipalName");
					if (attrLogin != null) {
						String login = (String) attrLogin.get();
						if (login.indexOf("@") > -1) {
							login = new StringTokenizer(login, "@").nextToken();
						}
						vo = getDadosUsuario(login, "");
						break;
					}
				}
			} 
			ctx.close();
		} catch (NamingException e) {
			throw new RuntimeException(e);
		} 
		return vo;
	}
	
	protected List<UsuarioVO> consultaLDAPTotal(InitialLdapContext ctx, String searchBase, String searchFilter, String[] objAttribs, int pageSize) throws NamingException, IOException {
		List<UsuarioVO> usuarios = new ArrayList<UsuarioVO>();
		PagedResultsControl[] ctls = new PagedResultsControl[]{new PagedResultsControl(pageSize, false)};
		SearchControls srchInfo = new SearchControls();
		srchInfo.setSearchScope(SearchControls.SUBTREE_SCOPE);
		srchInfo.setReturningAttributes(objAttribs);
		ctx.setRequestControls(ctls);
		byte[] cookie = null;
		do {
			NamingEnumeration dirObjects = ctx.search(searchBase, searchFilter, srchInfo);
			
			materializaAllElements(usuarios, dirObjects);
			
			// examine the response controls
            cookie = parseControls(ctx.getResponseControls());

            // pass the cookie back to the server for the next page
            ctx.setRequestControls(new Control[]{new PagedResultsControl(pageSize, cookie, Control.CRITICAL) });			
		} while((cookie != null) && (cookie.length > 0));
		
		
		
		return usuarios;
	}	
	
	static byte[] parseControls(Control[] controls) throws NamingException {

		byte[] cookie = null;
		if (controls != null) {
			for (int i = 0; i < controls.length; i++) {
				if (controls[i] instanceof PagedResultsResponseControl) {
					PagedResultsResponseControl prrc = (PagedResultsResponseControl) controls[i];
					cookie = prrc.getCookie();
				}
			}
		}
		return (cookie == null) ? new byte[0] : cookie;

	}
	
	protected NamingEnumeration consultaLDAP(InitialLdapContext ctx, String searchBase, String searchFilter, String[] objAttribs) throws NamingException {
		SearchControls srchInfo = new SearchControls();
		srchInfo.setSearchScope(SearchControls.SUBTREE_SCOPE);
		srchInfo.setReturningAttributes(objAttribs);
		NamingEnumeration dirObjects = ctx.search(searchBase, searchFilter, srchInfo);
		return dirObjects;
	}
	
	protected String montaDCList() {
		String dcList = "";
		try {
			dcList = "DC=" + dominio.replaceAll("\\.", ",DC=");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return dcList;
	}
	
	protected InitialLdapContext criaInitialContext(String login, String senha)	throws ParametroException {
		InitialLdapContext context = null;
		Hashtable<String, String> ldapEnv = new Hashtable<String, String>();
		String urlDC = "ldap://" + host + ":" + porta + "/";
		String userName = login + "@" + dominio;
		ldapEnv.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		ldapEnv.put(Context.SECURITY_AUTHENTICATION, "simple");
		ldapEnv.put(Context.SECURITY_PRINCIPAL, userName);
		ldapEnv.put(Context.SECURITY_CREDENTIALS, senha);
		ldapEnv.put(Context.PROVIDER_URL, urlDC);
		try {
			InitialLdapContext ldapContext = new InitialLdapContext(ldapEnv, null);
			context = ldapContext;
		} catch (NamingException e) {
			e.printStackTrace();
		}
		return context;
	}
	
	protected boolean verificaContaAtiva(SearchResult dirObject) throws NamingException {
		boolean ret = true;
		if (dirObject.getAttributes().get("userAccountControl") == null) {
			return true;
		}
		String userAccountControl = (String) dirObject.getAttributes().get("userAccountControl").get();
		if (!USUARIO_ATIVO.equals(userAccountControl) && ! USUARIO_ATIVO_PASSWORD_NOT_REQUIRED.equals(userAccountControl)) {
			ret = false;
		}
		return ret;
	}	
	
}
