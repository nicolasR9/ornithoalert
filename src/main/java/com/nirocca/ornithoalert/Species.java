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
    TRAUERSEESCHWALBE(290),
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
    SUMPFROHRSAENGER(423),
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
    ZWERGSTRANDLAEUFER(243),
    LOEFFLER(42),
    KUESTENSEESCHWALBE(294),
    ZWERGSEESCHWALBE(296),
    SEEREGENPFEIFER(219),
    REGENBRACHVOGEL(222),
    STEINWAELZER(236),
    BIENENFRESSER(330),
    BASSTOELPEL(23),
    DREIZEHENMOEWE(286),
    TROTTELLUMME(301),
    TORDALK(300),
    EISSTURMVOGEL(10),
    HECKENBRAUNELLE(461),
    GEBIRGSSTELZE(474),
    ROTHALSTAUCHER(9),
    FITIS(445),
    GARTENROTSCHWANZ(395),
    BAUMPIEPER(467),
    TRAUERSCHNAEPPER(458),
    NACHTIGALL(388),
    DROSSELROHRSAENGER(421),
    BAUMFALKE(174),
    WANDERFALKE(173),
    WESPENBUSSARD(144),
    UFERSCHWALBE(355),
    GRAUSCHNAEPPER(457),
    WALDBAUMLAEUFER(381),
    GELBSPOETTER(429),
    SILBERMOEWE(272),
    STEINSCHMAETZER(399);


    private int ornithoSpeciesId;

    public int getOrnithoSpeciesId() {
        return ornithoSpeciesId;
    }

    private Species(int ornithoSpeciesId) {
        this.ornithoSpeciesId = ornithoSpeciesId;
    }
}


