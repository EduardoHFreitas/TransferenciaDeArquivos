package br.univel.jshare.comum;

import java.io.Serializable;
import java.util.Date;

public class Arquivo implements Serializable {

	private static final long serialVersionUID = -6433462679928213637L;

	private long id;

	private String path;
	private String nome;
	private String extensao;

	private long tamanho;

	private String md5;

	private Date dataHoraModificacao;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getExtensao() {
		return extensao;
	}

	public void setExtensao(String extensao) {
		this.extensao = extensao;
	}

	public long getTamanho() {
		return tamanho;
	}

	public void setTamanho(long tamanho) {
		this.tamanho = tamanho;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public Date getDataHoraModificacao() {
		return dataHoraModificacao;
	}

	public void setDataHoraModificacao(Date dataHoraModificacao) {
		this.dataHoraModificacao = dataHoraModificacao;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dataHoraModificacao == null) ? 0 : dataHoraModificacao.hashCode());
		result = prime * result + ((extensao == null) ? 0 : extensao.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((md5 == null) ? 0 : md5.hashCode());
		result = prime * result + ((nome == null) ? 0 : nome.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		result = prime * result + (int) (tamanho ^ (tamanho >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Arquivo other = (Arquivo) obj;
		if (dataHoraModificacao == null) {
			if (other.dataHoraModificacao != null)
				return false;
		} else if (!dataHoraModificacao.equals(other.dataHoraModificacao))
			return false;
		if (extensao == null) {
			if (other.extensao != null)
				return false;
		} else if (!extensao.equals(other.extensao))
			return false;
		if (id != other.id)
			return false;
		if (md5 == null) {
			if (other.md5 != null)
				return false;
		} else if (!md5.equals(other.md5))
			return false;
		if (nome == null) {
			if (other.nome != null)
				return false;
		} else if (!nome.equals(other.nome))
			return false;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		if (tamanho != other.tamanho)
			return false;
		return true;
	}

}
