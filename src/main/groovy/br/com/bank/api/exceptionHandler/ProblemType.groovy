package br.com.bank.api.exceptionHandler

enum ProblemType {

    RESOURCE_NOT_FOUND("/recurso-nao-encontrado", "Recurso não encontrado"),
    INVALID_DATA("/dados-invalidos", "Dados inválidos"),
    EXISTS_RESOURCE_EQUALS("/recurso-existente", "Recurso existente"),
    NO_CREDIT_AVAILABLE("/credito-indisponivel", "Crédito indisponível"),
    UNHANDLED_ERROR("/erro-nao-tratado", "Erro não tratado")

    String title

    String path

    ProblemType(String path, String title) {
        this.path = path
        this.title = title
    }

}