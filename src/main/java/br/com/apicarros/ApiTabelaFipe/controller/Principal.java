package br.com.apicarros.ApiTabelaFipe.controller;

import br.com.apicarros.ApiTabelaFipe.model.Dados;
import br.com.apicarros.ApiTabelaFipe.model.Modelos;
import br.com.apicarros.ApiTabelaFipe.model.Veiculos;
import br.com.apicarros.ApiTabelaFipe.service.ConsumindoApi;
import br.com.apicarros.ApiTabelaFipe.service.CoverteDados;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;


public class Principal {

   private Scanner mostrarMenu = new Scanner(System.in);
   private ConsumindoApi consumo = new ConsumindoApi();
    private final String URL_BASE = "https://parallelum.com.br/fipe/api/v1/";
    private CoverteDados conversor = new CoverteDados();

    public void exibeMenu(){
       var menu = """ 
               *** OPÇÕES DE BUSCA: ***
               Carro
               Moto
               Caminhão
              
              
              Digite uma das opções para fazer sua busca:
               """;
       System.out.println(menu);

       var opcaoDigitada = mostrarMenu.nextLine();
       String endereco;

       if (opcaoDigitada.toLowerCase().contains("carr")){
           endereco = URL_BASE + "carros/marcas";
       }else if (opcaoDigitada.toLowerCase().contains("mot")){
           endereco = URL_BASE + "motos/marcas";
       }else{
           endereco = URL_BASE + "caminhoes/marcas";
       }

       var json = consumo.obtendoDados(endereco);
       System.out.println(json);
       var marcas = conversor.obterLista(json, Dados.class);
       marcas.stream()
               .sorted(Comparator.comparing(Dados::codigo))
               .forEach(System.out::println);

       System.out.println("Informe o código da marca do Veiculo para consulta: ");
       var codigoMarca = mostrarMenu.nextLine();

       endereco = endereco + "/" + codigoMarca + "/modelos";
       json = consumo.obtendoDados(endereco);
       var modeloLista = conversor.obeterDados(json, Modelos.class);

       System.out.println("\nModelos dessa marca: ");
       modeloLista.modelos().stream()
               .sorted(Comparator.comparing(Dados::codigo))
               .forEach(System.out::println);

        System.out.println("##################################################################");

       System.out.println("\nDigite o nome do veiculo que deseja buscado ! Obs: pode ser apenas um trecho do nome: ");
       var nomeVeiculo = mostrarMenu.nextLine();

       List<Dados> modelosFiltrados = modeloLista.modelos().stream()
               .filter(m -> m.nome().toLowerCase().contains(nomeVeiculo.toLowerCase()))
               .collect(Collectors.toList());

       System.out.println("\nModelos filtrados");
       modelosFiltrados.forEach(System.out::println);
        System.out.println("##################################################################");


       System.out.println("Digite por favor o código do modelo para buscar os valores de avaliação do veiculo: ");
       var codigoModelo = mostrarMenu.nextLine();

       endereco = endereco + "/" + codigoModelo + "/anos";
       json = consumo.obtendoDados(endereco);
       List<Dados> anos = conversor.obterLista(json, Dados.class);
       List<Veiculos> veiculos = new ArrayList<>();

       for (int i = 0; i < anos.size(); i++) {
           var enderecoAnos = endereco + "/" + anos.get(i).codigo();
           json = consumo.obtendoDados(enderecoAnos);
           Veiculos veiculo = conversor.obeterDados(json, Veiculos.class);
           veiculos.add(veiculo);

       }
        System.out.println("##################################################################");

       System.out.println("\nTodos os veículos filtrados com avaliações por ano : ");
       veiculos.forEach(System.out::println);


   }

}
