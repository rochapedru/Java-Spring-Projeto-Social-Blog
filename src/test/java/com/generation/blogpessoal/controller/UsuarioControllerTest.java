package com.generation.blogpessoal.controller;

import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.model.UsuarioLogin;
import com.generation.blogpessoal.repository.UsuarioRepository;
import com.generation.blogpessoal.service.UsuarioService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UsuarioControllerTest {
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private TestRestTemplate testRestTemplate;
    @BeforeAll
    void start() {
        usuarioRepository.deleteAll();
        usuarioService.cadastrarUsuario(new Usuario(0L,"Root","root@root.com","rootroot",""));
    }
    @Test
    @DisplayName("Cadastrar User")
    public void deveCriarUmUsuario(){
        /* requisição */
        HttpEntity<Usuario> corpoResquisicao = new HttpEntity<Usuario>(new Usuario(0L,"Marcelo","Marcelinho@email.com","12345678",""));
        /* Requisição HTTP */
        ResponseEntity<Usuario> corpoResposta = testRestTemplate.exchange("/usuarios/cadastrar", HttpMethod.POST, corpoResquisicao, Usuario.class);
        assertEquals(HttpStatus.CREATED, corpoResposta.getStatusCode());
    }
    @Test
    @DisplayName("n duplicar user")
    public void naoDeveDuplicarUsuario(){
        usuarioService.cadastrarUsuario(new Usuario(0L,"Lucas","Lucass@email.com","1234567890",""));
        /* Corpo da Requisição */
        HttpEntity<Usuario> corpoResquisicao = new HttpEntity<Usuario>(new Usuario(0L,"Lucas","Lucass@email.com","1234567890",""));
        /* Requisição HTTP */
        ResponseEntity<Usuario> corpoResposta = testRestTemplate.exchange("/usuarios/cadastrar", HttpMethod.POST, corpoResquisicao, Usuario.class);
        assertEquals(HttpStatus.BAD_REQUEST, corpoResposta.getStatusCode());
    }
    @Test
	@DisplayName("Atualizar um Usuário")
	public void deveAtualizarUmUsuario() {

		Optional<Usuario> usuarioCadastrado = usuarioService.cadastrarUsuario(new Usuario(0L, 
			"Kendal Katherine", "kendal_kat@email.com.br", "1234556789", ""));

		Usuario usuarioUpdate = new Usuario(usuarioCadastrado.get().getId(), 
				"Kendal Katherine Correira", "kendal_kath@email.com.br", "123456789", "");
		
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(usuarioUpdate);

		ResponseEntity<Usuario> corpoResposta = testRestTemplate
			.withBasicAuth("root@root.com", "rootroot")
			.exchange("/usuarios/atualizar", HttpMethod.PUT, corpoRequisicao, Usuario.class);

		assertEquals(HttpStatus.OK, corpoResposta.getStatusCode());
		
	}
    @Test
    @DisplayName("Listar todos Usuário")
    public void deveListarTodosOsUsuarios(){
        usuarioService.cadastrarUsuario(new Usuario(0L,"joao","joao@pedro.com","1234567890",""));
        usuarioService.cadastrarUsuario(new Usuario(0L,"john","john@email.com","1234567890",""));
        usuarioService.cadastrarUsuario(new Usuario(0L,"apollo","poloopollo@email.com","1234567890",""));
        ResponseEntity<String> corpoResposta = testRestTemplate.withBasicAuth("root@root.com", "rootroot")
                .exchange("/usuarios/all", HttpMethod.GET, null, String.class);
        assertEquals(HttpStatus.OK, corpoResposta.getStatusCode());
    }
    


    @Test
    @DisplayName("Buscar User Por ID")
    public void deveBuscarUsuarioId() {
        usuarioService.cadastrarUsuario(new Usuario(0L, "Math Math", "MathMath@email.com.br", "1234567890", ""));
        ResponseEntity<String> corpoResposta = testRestTemplate.withBasicAuth("root@root.com", "rootroot").exchange("/usuarios/1", HttpMethod.GET, null, String.class);

        assertEquals(HttpStatus.OK, corpoResposta.getStatusCode());
    }
}
