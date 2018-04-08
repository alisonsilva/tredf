package br.jus.tredf.justicanumeros.model.exception;

public interface ICodigosErros {
  public static final int ERRO_USUARIO_INCLUIR = 111;
  public static final int ERRO_USUARIO_RECUPERAR = 121;
  public static final int ERRO_USUARIO_PARAMETROINVALIDO = 112;
  
  public static final int ERRO_GRUPO_RECUPERARPORLOGIN = 211;
  public static final int ERRO_GRUPO_INSERIRGRUPO = 212;
  public static final int ERRO_GRUPO_REMOVERGRUPO = 221;
  public static final int ERRO_GRUPO_PARAMETROINVALIDO = 231;
  public static final int ERRO_GRUPO_PERMISSAOGRUPO = 213;
  public static final int ERRO_GRUPO_INSUSUARIOGRUPO = 214;
  public static final int ERRO_GRUPO_INSPERMGRUPO = 215;
  
  public static final int ERRO_SERVENTIAS_INCLUIR = 311;
  public static final int ERRO_SERVENTIAS_RECUPERAR = 321;
  public static final int ERRO_SERVENTIAS_INCLUIRLOG = 312;
  public static final int ERRO_SERVENTIAS_NOVASERVENTIA = 331;
  public static final int ERRO_SERVENTIAS_PARAMETROSINVALIDOS = 332;
  public static final int ERRO_SERVENTIAS_FECHADOPREENCHIMENTO = 313;
  
  public static final int ERRO_PERMISSAO_INSERIRPERMISSAO = 411;
  public static final int ERRO_PERMISSAO_REMOVERPERMISSAO = 421;
  public static final int ERRO_PERMISSAO_INSERIRPERMISSAOGRUPO = 431;
  public static final int ERRO_PERMISSAO_REMOVERPERMISSAOGRUPO = 441;
  public static final int ERRO_PERMISSAO_RECUPERARPERMISSOESGRUPO = 451;
  
  public static final int ERRO_LOGACOES_INSERIRLOG = 511;
  
  public static final int ERRO_FORMULARIOSSERVICE_VALIDARPERMUSUARIO = 511;
  public static final int ERRO_FORMULARIOSSERVICE_VALIDARPERMUSUARIO_SEMPERMISSAO = 512;  
  public static final int ERRO_FORMULARIOSSERVICE_INCPRODUCAOMAGISTRADO = 521;
  
  public static final int ERRO_CRIPTACAO_TOKENERROR = 611;
  public static final int ERRO_TIMESTAMP_INVALIDO = 612;
  
  public static final int ERRO_FORMULARIO_ALTERARFORMULARIO = 711;
  
  public static final int ERRO_PROTOCOLO_PARAMETROSINVALIDOS = 811;
  public static final int ERRO_PROTOCOLO_PROTOCOLODUPLICADO = 812;
  
  public static final int ERRO_SADP_PARAMETROINVALIDO = 911;
  public static final int ERRO_SADP_PROTOCOLOINVALIDO = 912;
  
  public static final int ERRO_TERCEIRIZADO_PARAMETROINVALIDO = 10101;
  public static final int ERRO_TERCEIRIZADO_JAEXISTENTE = 10102;
  
  public static final int ERRO_LOGACOES_TERCEIRIZADO = 10111;
  
  public static final int ERRO_JUSTIFICATIVAS_PARAMETROINVALIDO = 10121; 
  
  public static final int ERRO_ENVIOXML_PARAMETROINVALIDO = 10131;
  public static final int ERRO_ENVIOXML_NAOHADADOS = 10132;
  public static final int ERRO_ENVIOXML_ARQUIVOJACRIADO = 10133;
  
  public static final int REPORT_ERROR_GENERATION = 10122;
  
  public static final int ERRO_SAO_EXECUCAO = 10141;
}
