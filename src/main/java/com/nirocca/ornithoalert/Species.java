package com.nirocca.ornithoalert;

public enum Species {
    //find species id: https://www.ornitho.de/index.php?m_id=15&showback=stor&backlink=skip&y=2019&frmSpecies=176&sp_tg=1 (frmSpecies)

    WALDSCHNEPFE(240),
    WIESENPIEPER(463),
    TURTELTAUBE(311),
    BEUTELMEISE(378),
    FELDSCHWIRL(417),
    ORTOLAN(527),
    SPERBERGRASMUECKE(434),
    SCHWARZSTORCH(41),
    REBHUHN(188),
    WACHTEL(189),
    BRACHPIEPER(465),
    WALDWASSERLAEUFER(231),
    FLUSSUFERLAEUFER(233),
    BRUCHWASSERLAEUFER(232),
    ALPENSTRANDLAEUFER(249),
    KAMPFLAEUFER(253),
    SANDERLING(241),
    SCHLAGSCHWIRL(418),
    MERLIN(176),
    PRACHTTAUCHER(2),
    STERNTAUCHER(1),
    ZWERGDOMMEL(37),
    ROHRDOMMEL(38),
    BERGPIEPER(469),
    KURZSCHNABELGANS(65),
    ROTHALSGANS(73),
    WALDOHREULE(322),
    SUMPFOHREULE(323),
    SCHLEIEREULE(315),
    KIEBITZREGENPFEIFER(215),
    GOLDREGENPFEIFER(216),
    ZWERGSCHWAN(53),
    ZWERGMOEWE(284),
    HERINGSMOEWE(275),
    WIESENWEIHE(165),
    ROTFUSSFALKE(177),
    WEISSBARTSEESCHWALBE(288),
    WEISSFLUEGELSEESCHWALBE(289),
    BRANDSEESCHWALBE(298),
    BERGENTE(123),
    EISENTE(129),
    TRAUERENTE(129),
    OHRENTAUCHER(6),
    GELBSCHNABELTAUCHER(4),
    BERGHAENFLING(503),
    BIENENFRESSER(330);


    private int ornithoSpeciesId;

    public int getOrnithoSpeciesId() {
        return ornithoSpeciesId;
    }

    private Species(int ornithoSpeciesId) {
        this.ornithoSpeciesId = ornithoSpeciesId;
    }
}


