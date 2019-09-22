package model

enum class Genaration(val geName: String, val maxNumber: Int) {
    G1("Generation 1", 151),
    G2("Generation 2", 251),
    G3("Generation 3", 386),
    G4("Generation 4", 493),
    G5("Generation 5", 649),
    G6("Generation 6", 721),
    G7("Generation 7", 809);

    companion object {
        fun getArray(): Array<Genaration> {
            return values().asList().toTypedArray()
        }
    }
}