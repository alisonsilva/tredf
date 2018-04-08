package br.jus.tredf.justicanumeros.util;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.ResourceBundle;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sun.misc.BASE64Decoder;
import br.jus.tredf.justicanumeros.model.exception.ICodigosErros;
import br.jus.tredf.justicanumeros.model.exception.ParametroException;
import br.jus.tredf.justicanumeros.model.wrapper.UsuarioVO;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.sun.jersey.core.spi.scanning.uri.BundleSchemeScanner;


@Service(value="authenticationService")
@SuppressWarnings("all")
public class AuthenticationService {
	@Autowired
	private ResourceBundle bundle;
	
  @Autowired
  private PropertiesServiceController properties;

  private final String cipherTransformation = "AES/CBC/NoPadding";    
  
  private final byte[] keyValue = new byte[]{'F', 'E', '1', 'B', 'D', '5', '7', '5', '6', '2', '7', 'D', 'F', 'a', '1', 'c'};
  private final byte[] ivValue = new byte[]{'f', 'e', 'd', 'c', 'b', 'a', '9', '8', '7', '6', '5', '4', '3', '2', '1', '0'};
  
  private IvParameterSpec ivspec = new IvParameterSpec(ivValue);
  private SecretKeySpec keyspec = new SecretKeySpec(keyValue, "AES");
    
	private Cipher cipher = null;

	public AuthenticationService() {
		if (cipher == null) {
			try {
				cipher = Cipher.getInstance(cipherTransformation);
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (NoSuchPaddingException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void validaToken(String token) {
		
		if (StringUtils.isEmpty(token)) {
			throw new ParametroException(bundle.getString("AuthenticationService.validaToken.tokenVazio"), 
			    ICodigosErros.ERRO_CRIPTACAO_TOKENERROR);
		} 
		
		String valores = decrypt(token);
		
		if (StringUtils.isEmpty(valores)) {
			throw new ParametroException(bundle.getString("AuthenticationService.validaToken.tokenNaoDecriptavel"), 
			    ICodigosErros.ERRO_CRIPTACAO_TOKENERROR);
		}
		
		UsuarioVO usrVo = null;
		try {
			Gson gson = new Gson();
			JsonReader reader = new JsonReader(new StringReader(valores));
			reader.setLenient(true);
			usrVo = gson.fromJson(reader, UsuarioVO.class);
		} catch (Exception e) {
			throw new ParametroException(
			    MessageFormat.format(
			        bundle.getString("AuthenticationService.validaToken.formatoinvalid"), e.getMessage()), 
			    ICodigosErros.ERRO_CRIPTACAO_TOKENERROR);
		}
		
		if(usrVo != null) {
			String usuario = usrVo.lgn.toLowerCase();
			String password = usrVo.sn;
			String timestamp = usrVo.tm;

			boolean estatico = false;
			String tempoOcioso = properties.getProperty("auth.tempo.ocioso");
			
			if (StringUtils.isEmpty(timestamp)) {// se não vier o timestamp da requisição, erro
				throw new ParametroException(bundle.getString("AuthenticationService.validaToken.tempoDeEnvioInvalido"), 
				    ICodigosErros.ERRO_CRIPTACAO_TOKENERROR);
			} else {
				Date data = new Date(Long.parseLong(timestamp));
				Date dataAtual = new Date();
				if (((dataAtual.getTime()/60000) - (data.getTime()/60000)) > Integer.valueOf(tempoOcioso)) {//se a requisição tiver mais que cinco minutos, erro
					throw new ParametroException(bundle.getString("AuthenticationService.validaToken.timestampInvalido"), 
					    ICodigosErros.ERRO_TIMESTAMP_INVALIDO);
				}
			}
		} else {
			throw new ParametroException(
			    MessageFormat.format(
			        bundle.getString("AuthenticationService.validaToken.mensagemDefault"), "n�o foi poss�vel criar token"), 
			    ICodigosErros.ERRO_CRIPTACAO_TOKENERROR);
		}
	}
	
	public UsuarioVO getUsuarioFromToken(String token) {
		String valores = decrypt(token);
		
		if (StringUtils.isEmpty(valores)) {
			throw new ParametroException(bundle.getString("AuthenticationService.getUsuarioFromToken.tokenNaoDecriptavel"), 
			    ICodigosErros.ERRO_CRIPTACAO_TOKENERROR);
		}
		
		UsuarioVO usrVo = null;
		try {
			Gson gson = new Gson();
			JsonReader reader = new JsonReader(new StringReader(valores));
			reader.setLenient(true);
			usrVo = gson.fromJson(reader, UsuarioVO.class);
			usrVo.lgn = usrVo.lgn.toLowerCase();
		} catch (Exception e) {
			throw new ParametroException(
			    MessageFormat.format(
			        bundle.getString("AuthenticationService.getUsuarioFromToken.mensagemDefault"), 
			          "formato dos valores inv�lidos (" + e.getMessage() +")"), 
			        ICodigosErros.ERRO_CRIPTACAO_TOKENERROR);
		}
		return usrVo;
	}
	
	public String criaToken(String login, String pass) {
		if (StringUtils.isEmpty(login) || StringUtils.isEmpty(pass)) {
			throw new ParametroException("Login vazio ou senha vazia", 1);
		}
		String token = null;
		try {
			String forma = "{lgn: " + login.toLowerCase() + ", sn: " + pass + ", tm: " + System.currentTimeMillis() + "}";
			String formaPadded = padding(forma);
			cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
			byte[] encrypted = cipher.doFinal(formaPadded.getBytes());
      token = new String(Base64.encodeBase64(encrypted));
      token = token.replace("+", "_p_");
      token = token.replace("/", "_b_");
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		}
		return token;
	}
	
	public String encrypt(String palavra) {
		if (StringUtils.isEmpty(palavra)) {
			throw new ParametroException("A palavra a ser criptografada não pode estar vazia", 1);
		}
		String token = null;
		try {
			String formaPadded = padding(palavra);
			cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
			byte[] encrypted = cipher.doFinal(formaPadded.getBytes());
      token = new String(Base64.encodeBase64(encrypted));
      token = token.replace("+", "_p_");
      token = token.replace("/", "_b_");
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		}
		return token;
	}
	
	private String padding(String stringToPad) {
		int size = 16;
		String paddingChar = " ";
		int x = stringToPad.length() % size;
		int padLength = size - x;
		for(int i = 0; i < padLength; i++) {
			stringToPad += paddingChar;
		}
		return stringToPad;
	}
	
	
	public String decrypt(String encryptedData)	{
		String decryptedValue = null; 
        try {
			cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);
			encryptedData = encryptedData.replace("_p_", "+");
			encryptedData = encryptedData.replace("_b_", "/");
			byte[] decordedValue = new BASE64Decoder().decodeBuffer(encryptedData);
			byte[] decValue = cipher.doFinal(decordedValue);
			decryptedValue = new String(decValue, "UTF-8");
		} catch (InvalidKeyException e) {
		  throw new ParametroException(MessageFormat.format(
		      bundle.getString("AuthenticationService.getUsuarioFromToken.mensagemDefault"), 
		      "valores de acesso inv�lidos (" + e.getMessage() + ")"), 
      ICodigosErros.ERRO_CRIPTACAO_TOKENERROR);
		} catch (InvalidAlgorithmParameterException e) {
      throw new ParametroException(MessageFormat.format(
          bundle.getString("AuthenticationService.getUsuarioFromToken.mensagemDefault"), 
          "valores de acesso inv�lidos (" + e.getMessage() + ")"), 
      ICodigosErros.ERRO_CRIPTACAO_TOKENERROR);
		} catch (IllegalBlockSizeException e) {
      throw new ParametroException(MessageFormat.format(
          bundle.getString("AuthenticationService.getUsuarioFromToken.mensagemDefault"), 
          "valores de acesso inv�lidos (" + e.getMessage() + ")"), 
      ICodigosErros.ERRO_CRIPTACAO_TOKENERROR);
		} catch (BadPaddingException e) {
      throw new ParametroException(MessageFormat.format(
          bundle.getString("AuthenticationService.getUsuarioFromToken.mensagemDefault"), 
          "valores de acesso inv�lidos (" + e.getMessage() + ")"), 
      ICodigosErros.ERRO_CRIPTACAO_TOKENERROR);
		} catch (UnsupportedEncodingException e) {
      throw new ParametroException(MessageFormat.format(
          bundle.getString("AuthenticationService.getUsuarioFromToken.mensagemDefault"), 
          "valores de acesso inv�lidos (" + e.getMessage() + ")"), 
      ICodigosErros.ERRO_CRIPTACAO_TOKENERROR);
		} catch (IOException e) {
      throw new ParametroException(MessageFormat.format(
          bundle.getString("AuthenticationService.getUsuarioFromToken.mensagemDefault"), 
          "valores de acesso inv�lidos (" + e.getMessage() + ")"), 
      ICodigosErros.ERRO_CRIPTACAO_TOKENERROR);
		}
    return decryptedValue;
	}	
	
}
