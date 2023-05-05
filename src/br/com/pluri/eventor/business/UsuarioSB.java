package br.com.pluri.eventor.business;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.etechoracio.common.business.BaseSB;
import br.com.etechoracio.common.view.MessageBundleLoader;
import br.com.pluri.eventor.business.exception.CampoObrigatórioException;
import br.com.pluri.eventor.business.exception.LoginJaCadastradoException;
import br.com.pluri.eventor.business.exception.SenhaInvalidaException;
import br.com.pluri.eventor.business.util.PasswordUtils;
import br.com.pluri.eventor.dao.UsuarioDAO;
import br.com.pluri.eventor.model.Atividade;
import br.com.pluri.eventor.model.Usuario;

@Service
public class UsuarioSB extends BaseSB {
	
	private UsuarioDAO usuarioDAO;
	
	private Usuario resultValidarSenha = new Usuario();

	@Override
	protected void postConstructImpl() {
		usuarioDAO = getDAO(UsuarioDAO.class);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(Usuario usuario) throws LoginJaCadastradoException, SenhaInvalidaException, CampoObrigatórioException {
		List<Usuario> result = usuarioDAO.findByLogin(usuario.getLogin());
		if (CollectionUtils.isEmpty(result)) {
			if(usuario.getSenha() == null) {
				throw new CampoObrigatórioException(MessageBundleLoader.getMessage("crítica.camposobrigatorios", new Object[] {"'Senha'"}));
			}
			if(usuario.getConfirmSenha() == null) {
				throw new CampoObrigatórioException(MessageBundleLoader.getMessage("crítica.camposobrigatorios", new Object[] {"'Confirma Senha'"}));
			}
			if (PasswordUtils.criptografarMD5(usuario.getConfirmSenha()).equals(usuario.getSenha())){
				throw new SenhaInvalidaException(MessageBundleLoader.getMessage("critica.senhaincorreta"));
			}
			usuario.setSenha(PasswordUtils.criptografarMD5(usuario.getSenha()));
			usuario.setDataAlter(getDateAlter());
			usuarioDAO.save(usuario);
		} else {
			throw new LoginJaCadastradoException(MessageBundleLoader.getMessage("critica.loginjacadastrado", new Object[] {usuario.getLogin()}));
		}
	}
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<Usuario> findAll() {
		return usuarioDAO.findAll();
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void editeUsuario(Usuario usuario) throws SenhaInvalidaException, LoginJaCadastradoException {
		if(usuario.getCpfCnpj().contains("_")){
			usuario.setCpfCnpj("");
		} else {
			usuario.setCpfCnpj(usuario.getCpfCnpj().replace(".", "").replace("-", ""));
		}
		if(usuario.atualizaSenha.equals("S")){
			usuario.setSenha(PasswordUtils.criptografarMD5(usuario.getSenha()));
			if (PasswordUtils.criptografarMD5(usuario.getOldsenha()).equals(usuario.getSenha())) {
				throw new SenhaInvalidaException(MessageBundleLoader.getMessage("critica.senhausada"));
			}
			if (!validarSenhaOld(usuario)){
				throw new SenhaInvalidaException(MessageBundleLoader.getMessage("critica.senhaincorreta"));
			}
		}
		if (!usuario.loginVerificado) {
			throw new LoginJaCadastradoException(MessageBundleLoader.getMessage("critica.loginnotverifiqued", new Object[] {usuario.getLogin()}));
		} else {
			usuario.setDataAlter(getDateAlter());
			usuarioDAO.save(usuario);
		}
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Usuario findById(Long idUsuarioLogado) {
		return usuarioDAO.findById(idUsuarioLogado);
	}

	public boolean validarSenhaOld(Usuario usuario) {
		resultValidarSenha = usuarioDAO.findByIdAndSenha(usuario.getId(), PasswordUtils.criptografarMD5(usuario.getOldsenha()));
		if(resultValidarSenha!=null && usuario.getSenha().equals(PasswordUtils.criptografarMD5(usuario.getConfirmSenha()))){
			return true;
		} else {
			return false;
		}
	}
	
	@Transactional(propagation=Propagation.NOT_SUPPORTED)
	public List<Usuario> findIncritosByAtividade(Atividade ativ) {
		return usuarioDAO.findIncritosNaAtividade(ativ.getId());
	}
	
	public Usuario findUsuarioByLogin(String login) {
		return usuarioDAO.findByLogin_v2(login);
	}
}
