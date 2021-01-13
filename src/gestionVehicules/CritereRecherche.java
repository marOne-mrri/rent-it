package gestionVehicules;

/*
   *DISPONIBLE: vehicule qu'on peut reserver ou louer directement.
   *DISPONIBLE_SANS_PARKING: vehicule disponible mais en dehors de tout parking de l'agence, a noter
        qu'on a donner aux utilisateurs l'option d'ajouter un vehicule sans parking qui siginfe
        vehicule existe mais pas dans un parking de l'agence car il se peut que tous les parkings
        sont pleins ou bien un vehicule est chez le client.
   *RESERVE: vehicule reserve ou bien dans un contrat de location mais il est toujours chez
        l'agence en attendant soit la validation de la reservation soit que client arrive
        pour le prendre
   *EN_LOCATION: apres avoir ete reserve maintenant véhicule est chez client, dans ce cas seulement
        l'utilisateur peut le restituer pour que ce vehicule devient disponible encore une fois
 */
public enum CritereRecherche {
    TOUS, DISPONIBLE, DISPONIBLE_SANS_PARKING, RESERVE, EN_LOCATION
}