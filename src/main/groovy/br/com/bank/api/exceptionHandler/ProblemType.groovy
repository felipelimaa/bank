package br.com.bank.api.exceptionHandler

enum ProblemType {

    RESOURCE_NOT_FOUND("/recurso-nao-encontrado", "Recurso não encontrado"),
    INVALID_DATA("/dados-invalidos", "Dados inválidos")

    String title

    String path

    ProblemType(String path, String title) {
        this.path = path
        this.title = title
    }

}