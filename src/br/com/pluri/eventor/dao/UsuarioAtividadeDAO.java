package br.com.pluri.eventor.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.etechoracio.common.dao.BaseDAO;
import br.com.pluri.eventor.model.UsuarioAtividade;

@Repository
public interface UsuarioAtividadeDAO extends BaseDAO<UsuarioAtividade> {

		@Query("select u from UsuarioAtividade u " +
			   " inner join u.atividade a " +
			   " inner join a.evento e " +
			   " where e.usuario.id = :id")
		public List<UsuarioAtividade> findIncritosNoEventoByUsuarioLogado (@Param("id") Long idUsu);
		
		@Query("select u from UsuarioAtividade u " +
			   " where u.usuario.id = :idUsu and u.atividade.id = :idAtiv")
		public UsuarioAtividade findSeEstaInscritoNaAtividade(@Param("idUsu") Long idUsu, @Param("idAtiv") Long idAtiv);
		
		@Query("select u from UsuarioAtividade u " +
			   " where u.atividade.id = :idAtiv")
		public List<UsuarioAtividade> findAllInscritosByIdAtividade(@Param("idAtiv") Long idAtiv);
		
		@Query("select u from UsuarioAtividade u " +
			   " where u.usuario.id = :idUsu")
		public List<UsuarioAtividade> findMyInscricoes(@Param("idUsu") Long idUsu);
			
		@Query("select u from UsuarioAtividade u " +
			   " inner join u.atividade a " +
			   " inner join a.evento e " +
			   " where e.usuario.id = :idUsu and e.id = :idEven")
		public List<UsuarioAtividade> findAtividadesNoEventoByUsuarioLogado (@Param("idUsu") Long idUsu, @Param("idEven") Long idEven);
	
}