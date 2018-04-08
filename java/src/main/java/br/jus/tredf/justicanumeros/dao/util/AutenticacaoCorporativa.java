package br.jus.tredf.justicanumeros.dao.util;

import java.util.List;

import javax.naming.ldap.InitialLdapContext;

import br.jus.tredf.justicanumeros.model.wrapper.UsuarioVO;

public abstract class AutenticacaoCorporativa {

	public boolean autenticacaoCorporativa() {
		return false;
	}
	public abstract InitialLdapContext loginDominio(String login, String senha);
	public abstract UsuarioVO getDadosUsuario(String login, String senha);
	public abstract UsuarioVO getDadosUsuarioPorEmail(String email);
	public abstract List<UsuarioVO> getTodosUsuarios();
}
