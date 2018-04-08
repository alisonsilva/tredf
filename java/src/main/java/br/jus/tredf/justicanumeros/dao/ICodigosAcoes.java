package br.jus.tredf.justicanumeros.dao;

public interface ICodigosAcoes {
  public static final int ACAO_INSERIR_GRUPO = 211;
  public static final int ACAO_REMOVER_GRUPO = 212;
  public static final int ACAO_ALTERAR_GRUPO = 213;
  public static final int ACAO_NOVAPERMISSAO_GRUPO = 214;
  public static final int ACAO_NOVOUSUARIO_GRUPO = 215;
  public static final int ACAO_REMOVEPERMISSAO_GRUPO=216;
  public static final int ACAO_REMOVEUSUARIO_GRUPO=217;
  public static final int ACAO_ATUALIZAR_ARQUIVO_EXECUCAO = 218;
  public static final int ACAO_REMOVER_ARQUIVO_EXECUCAO = 219;
  public static final int ACAO_USUARIO_ACESSANDO_SISTEMA = 220;
  
  public static final int ACAO_TERCEIRIZADO_INSERCAO = 311;
  public static final int ACAO_TERCEIRIZADO_REMOCAO = 312;
  public static final int ACAO_TERCEIRIZADO_ALTERACAO = 313;
  
  public static final int ACAO_OBSERVACAO_INSERCAO = 411;
  public static final int ACAO_OBSERVACAO_REMOCAO = 412;
  public static final int ACAO_OBSERVACAO_ALTERACAO = 413;
  
  public static final int ACAO_ENVIOXML_CRIACAOARQUIVO = 511;
  public static final int ACAO_ENVIOXML_PRODUCAOPROCESSOSENVIO = 511;
  public static final int ACAO_ENVIOXML_ENVIOPROCESSOS = 512;
  public static final int ACAO_ENVIOXML_ENVIOPROCESSO = 513;
  public static final int ACAO_ENVIOXML_REMOVEENVIO = 514;
  
}
