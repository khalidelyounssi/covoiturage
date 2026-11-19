package ma.youcode.cov.web;



public final class Acteur {
    private Acteur() {}
    public static void verifierProprietaire(Long acteur, Long cible) {
        if (!cible.equals(acteur)) throw ApiError.interdit("Cette ressource appartient à un autre utilisateur");
    }
}