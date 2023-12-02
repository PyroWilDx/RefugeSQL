DROP TABLE Refuge CASCADE CONSTRAINTS;

CREATE TABLE Refuge (
    emailRef VARCHAR2(100) PRIMARY KEY,
    nomRef VARCHAR2(100),
    sectGeo VARCHAR2(50),
    dateStart DATE,
    dateEnd DATE,
    nbPlaceRepas INT,
    nbPlaceDormir INT,
    txtPres VARCHAR2(500),
    prixNuit INT,
    CONSTRAINT CHK_PositiveVal_r CHECK (nbPlaceRepas > 0),
    CONSTRAINT CHK_PositiveVal_r1 CHECK (nbPlaceDormir > 0),
    CONSTRAINT CHK_PositiveVal_r11 CHECK (prixNuit > 0),
    CONSTRAINT CHK_dateVal_r111 CHECK (dateEnd > dateStart)
);

DROP TABLE TypePaiement CASCADE CONSTRAINTS;

CREATE TABLE TypePaiement (
    typePaiement VARCHAR2(100) PRIMARY KEY,
    CONSTRAINT CHK_Type_paiment CHECK (
        typePaiement IN ('espèce', 'chèque', 'carte-bleue')
    )
);

DROP TABLE RefugeATypePaiement CASCADE CONSTRAINTS;

CREATE TABLE RefugeATypePaiement (
    emailRef VARCHAR2(100) NOT NULL,
    typePaiement VARCHAR2(100) NOT NULL,
    PRIMARY KEY(emailRef, typePaiement),
    FOREIGN KEY (emailRef) REFERENCES Refuge(emailRef),
    FOREIGN KEY (typePaiement) REFERENCES TypePaiement(typePaiement)
);

DROP TABLE Membre CASCADE CONSTRAINTS;

CREATE TABLE Membre (
    idUser INT PRIMARY KEY,
    emailUser VARCHAR2(100),
    MDP VARCHAR2(100),
    nomUser VARCHAR2(100),
    prenomUser VARCHAR2(100),
    adresse VARCHAR2(200),
    CONSTRAINT unique_mail_membre UNIQUE (emailUser)
);

DROP TABLE Formation CASCADE CONSTRAINTS;

CREATE TABLE Formation (
    anneeForm INT,
    rangForm INT,
    PRIMARY KEY (anneeForm, rangForm),
    nom VARCHAR2(100),
    dateStart DATE,
    duree INT,
    nbPlace INT,
    descFor VARCHAR2(500),
    CONSTRAINT CHK_PositivVal_r CHECK (duree > 0),
    CONSTRAINT CHK_PositivVal1_r CHECK (nbPlace > 0)
);

DROP TABLE Activite CASCADE CONSTRAINTS;

CREATE TABLE Activite (nomActivite VARCHAR2(100) PRIMARY KEY);

DROP TABLE ArbreCategorie CASCADE CONSTRAINTS;

CREATE TABLE ArbreCategorie (
    categorieFils VARCHAR2(100) PRIMARY KEY,
    categoriePere VARCHAR2(100)
);

DROP TABLE LotMateriel CASCADE CONSTRAINTS;

CREATE TABLE LotMateriel (
    marque VARCHAR2(100),
    modele VARCHAR2(100),
    anneeAchat INT,
    PRIMARY KEY (marque, modele, anneeAchat),
    nbPiece INT,
    prixAccident INT,
    categorie VARCHAR2(50) NOT NULL,
    FOREIGN KEY (categorie) REFERENCES ArbreCategorie(categorieFils),
    CONSTRAINT CHK_PostivvVal1_r CHECK (nbPiece >= 0),
    CONSTRAINT CHK_PostivvVal1_r1 CHECK (nbPiece >= 0)
);

DROP TABLE InfoMateriel CASCADE CONSTRAINTS;

CREATE TABLE InfoMateriel (infoMat VARCHAR2(200) PRIMARY KEY);

DROP TABLE LotMaterielPourActivites CASCADE CONSTRAINTS;

CREATE TABLE LotMaterielPourActivites (
    marque VARCHAR2(100) NOT NULL,
    modele VARCHAR2(100) NOT NULL,
    anneeAchat INT NOT NULL,
    nomActivite VARCHAR2(100) NOT NULL,
    PRIMARY KEY (marque, modele, anneeAchat, nomActivite),
    FOREIGN KEY (marque, modele, anneeAchat) REFERENCES LotMateriel(marque, modele, anneeAchat),
    FOREIGN KEY (nomActivite) REFERENCES Activite(nomActivite)
);

DROP TABLE FormationPossedeActivites CASCADE CONSTRAINTS;

CREATE TABLE FormationPossedeActivites (
    anneeForm INT NOT NULL,
    rangForm INT NOT NULL,
    nomActivite VARCHAR2(100) NOT NULL,
    PRIMARY KEY (anneeForm, rangForm, nomActivite),
    FOREIGN KEY (anneeForm, rangForm) REFERENCES Formation(anneeForm, rangForm),
    FOREIGN KEY (nomActivite) REFERENCES Activite(nomActivite)
);

DROP TABLE LotMaterielInfo CASCADE CONSTRAINTS;

CREATE TABLE LotMaterielInfo (
    marque VARCHAR2(100) NOT NULL,
    modele VARCHAR2(100) NOT NULL,
    AnneeAchat INT NOT NULL,
    infoMat VARCHAR2(200) NOT NULL,
    PRIMARY KEY (marque, modele, AnneeAchat),
    FOREIGN KEY (marque, modele, AnneeAchat) REFERENCES LotMateriel(marque, modele, AnneeAchat),
    FOREIGN KEY (infoMat) REFERENCES InfoMateriel(infoMat)
);

DROP TABLE PrixDejeuner CASCADE CONSTRAINTS;

CREATE TABLE PrixDejeuner (
    prixDejeuner INT PRIMARY KEY,
    CONSTRAINT CHK_PostivVl1_rdej CHECK (prixDejeuner >= 0)
);

DROP TABLE PrixSouper CASCADE CONSTRAINTS;

CREATE TABLE PrixSouper (
    prixSouper INT PRIMARY KEY,
    CONSTRAINT CHK_PostiVl1_r CHECK (prixSouper >= 0)
);

DROP TABLE PrixCasseCroute CASCADE CONSTRAINTS;

CREATE TABLE PrixCasseCroute (
    prixCasseCroute INT PRIMARY KEY,
    CONSTRAINT CHK_PostiV1_r CHECK (prixCasseCroute >= 0)
);

DROP TABLE PrixDiner CASCADE CONSTRAINTS;

CREATE TABLE PrixDiner (
    prixDiner INT PRIMARY KEY,
    CONSTRAINT CHK_PostiVl_r CHECK (prixDiner >= 0)
);

DROP TABLE RefugeAPrixDejeuner CASCADE CONSTRAINTS;

CREATE TABLE RefugeAPrixDejeuner (
    emailRef VARCHAR2(100) PRIMARY KEY,
    prixDejeuner INT NOT NULL,
    FOREIGN KEY (emailRef) REFERENCES Refuge(emailRef),
    FOREIGN KEY (prixDejeuner) REFERENCES PrixDejeuner(prixDejeuner)
);

DROP TABLE RefugeAPrixSouper CASCADE CONSTRAINTS;

CREATE TABLE RefugeAPrixSouper (
    emailRef VARCHAR2(100) PRIMARY KEY,
    prixSouper INT NOT NULL,
    FOREIGN KEY (emailRef) REFERENCES Refuge(emailRef),
    FOREIGN KEY (prixSouper) REFERENCES PrixSouper(prixSouper)
);

DROP TABLE RefugeAPrixCasseCroute CASCADE CONSTRAINTS;

CREATE TABLE RefugeAPrixCasseCroute (
    emailRef VARCHAR2(100) PRIMARY KEY,
    prixCasseCroute INT NOT NULL,
    FOREIGN KEY (emailRef) REFERENCES Refuge(emailRef),
    FOREIGN KEY (prixCasseCroute) REFERENCES PrixCasseCroute(prixCasseCroute)
);

DROP TABLE RefugeAPrixDiner CASCADE CONSTRAINTS;

CREATE TABLE RefugeAPrixDiner (
    emailRef VARCHAR2(100) PRIMARY KEY,
    prixDiner INT NOT NULL,
    FOREIGN KEY (emailRef) REFERENCES Refuge(emailRef),
    FOREIGN KEY (prixDiner) REFERENCES PrixDiner(prixDiner)
);

DROP TABLE Telephone CASCADE CONSTRAINTS;

CREATE TABLE Telephone (
    numTel VARCHAR2(20) PRIMARY KEY,
    emailRef VARCHAR2(100) NOT NULL,
    FOREIGN KEY (emailRef) REFERENCES Refuge(emailRef)
);

DROP TABLE ReservationRefuge CASCADE CONSTRAINTS;

CREATE TABLE ReservationRefuge (
    idRR INT PRIMARY KEY,
    dateRR DATE,
    heure INT,
    nbNuits INT,
    nbRepas INT,
    prixRes INT,
    idUser INT NOT NULL,
    emailRef VARCHAR2(100) NOT NULL,
    FOREIGN KEY (idUser) REFERENCES Membre(idUser),
    FOREIGN KEY (emailRef) REFERENCES Refuge(emailRef),
    CONSTRAINT CHK_PosnbNuits CHECK (nbNuits >= 0),
    CONSTRAINT CHK_PostnbRepas CHECK (nbRepas >= 0),
    CONSTRAINT CHK_PosprixRes CHECK (prixRes >= 0)
);

DROP TABLE RepasReserve CASCADE CONSTRAINTS;

CREATE TABLE RepasReserve (
    idRR INT NOT NULL,
    jour DATE,
    aReserveDej VARCHAR2(10),
    aReserveDiner VARCHAR2(10),
    aReserveSoup VARCHAR2(10),
    aReserveCC VARCHAR2(10),
    PRIMARY KEY (idRR, jour),
    FOREIGN KEY (idRR) REFERENCES ReservationRefuge(idRR)
);

DROP TABLE Adherent CASCADE CONSTRAINTS;

CREATE TABLE Adherent (
    idAdherent INT,
    idUser INT NOT NULL,
    emailUser VARCHAR2(100),
    MDP VARCHAR2(100),
    nomUser VARCHAR2(100),
    prenomUser VARCHAR2(100),
    adresse VARCHAR2(200),
    PRIMARY KEY (idAdherent, idUser),
    FOREIGN KEY (idUser) REFERENCES Membre(idUser)
);

DROP TABLE ReservationFormation CASCADE CONSTRAINTS;

CREATE TABLE ReservationFormation (
    idRF INT PRIMARY KEY,
    inscrit VARCHAR2(30),
    idAdherent INT NOT NULL,
    idUser INT NOT NULL,
    anneeForm INT NOT NULL,
    rangForm INT NOT NULL,
    FOREIGN KEY (idAdherent, idUser) REFERENCES Adherent(idAdherent, idUser),
    FOREIGN KEY (anneeForm, rangForm) REFERENCES Formation(anneeForm, rangForm),
    CONSTRAINT CHK_val_inscrit CHECK (inscrit IN ('validé', 'attente'))
);

DROP TABLE RangListeAttente CASCADE CONSTRAINTS;

CREATE TABLE RangListeAttente (
    rangLA INT PRIMARY KEY,
    CONSTRAINT CHK_PostVl_rrfj CHECK (rangLA >= 0)
);

DROP TABLE RangAttenteReservationForm CASCADE CONSTRAINTS;

CREATE TABLE RangAttenteReservationForm (
    idRF INT PRIMARY KEY,
    rangLA INT NOT NULL,
    FOREIGN KEY (idRF) REFERENCES ReservationFormation(idRF),
    FOREIGN KEY (rangLA) REFERENCES RangListeAttente(rangLA)
);

DROP TABLE LocationMateriel CASCADE CONSTRAINTS;

CREATE TABLE LocationMateriel (
    idLM INT PRIMARY KEY,
    nbPieceR INT,
    dateRecup DATE,
    dateRetour DATE,
    idAdherent INT NOT NULL,
    idUser INT NOT NULL,
    marque VARCHAR2(100) NOT NULL,
    modele VARCHAR2(100) NOT NULL,
    anneeAchat INT NOT NULL,
    FOREIGN KEY (idAdherent, idUser) REFERENCES Adherent(idAdherent, idUser),
    FOREIGN KEY (marque, modele, anneeAchat) REFERENCES LotMateriel(marque, modele, anneeAchat),
    CONSTRAINT CHK_PostnbPiece CHECK (nbPieceR >= 0),
    CONSTRAINT CHK_ostdateRetour CHECK (dateRetour >= dateRecup)
);

DROP TABLE NbPieceAccident CASCADE CONSTRAINTS;

CREATE TABLE NbPieceAccident (
    nb NUMBER PRIMARY KEY,
    CONSTRAINT CHK_Postnb CHECK (nb >= 0)
);

DROP TABLE NbPieceAccidentLocMat CASCADE CONSTRAINTS;

CREATE TABLE NbPieceAccidentLocMat (
    idLM INT PRIMARY KEY,
    nb INT NOT NULL,
    FOREIGN KEY (idLM) REFERENCES LocationMateriel(idLM),
    FOREIGN KEY (nb) REFERENCES NbPieceAccident(nb)
);

DROP TABLE Somme CASCADE CONSTRAINTS;

CREATE TABLE Somme (typeSO VARCHAR2(100) PRIMARY KEY);

DROP TABLE DescriptionSomme CASCADE CONSTRAINTS;

CREATE TABLE DescriptionSomme (descSO VARCHAR2(200) PRIMARY KEY);

DROP TABLE SommeAPourDescription CASCADE CONSTRAINTS;

CREATE TABLE SommeAPourDescription (
    TypeSO VARCHAR2(100) NOT NULL,
    descSO VARCHAR2(200) NOT NULL,
    PRIMARY KEY (TypeSO),
    FOREIGN KEY (TypeSO) REFERENCES Somme(TypeSO),
    FOREIGN KEY (descSO) REFERENCES DescriptionSomme(descSO)
);

DROP TABLE SommeDueUser CASCADE CONSTRAINTS;

CREATE TABLE SommeDueUser (
    idUser INT NOT NULL,
    TypeSO VARCHAR2(100) NOT NULL,
    prix INT NOT NULL,
    PRIMARY KEY (idUser, TypeSO),
    FOREIGN KEY (idUser) REFERENCES Membre(idUser),
    FOREIGN KEY (TypeSO) REFERENCES Somme(TypeSO)
);

DECLARE v_error_code NUMBER;

v_error_msg VARCHAR2(100);

BEGIN SAVEPOINT start_point;

INSERT INTO
    Refuge (
        emailRef,
        nomRef,
        sectGeo,
        dateStart,
        dateEnd,
        nbPlaceRepas,
        nbPlaceDormir,
        txtPres,
        prixNuit
    )
VALUES
    (
        'refuge4@example.com',
        'Refuge Berrechid',
        'Vallée',
        TO_DATE('2023-01-02', 'YYYY-MM-DD'),
        TO_DATE('2023-11-10', 'YYYY-MM-DD'),
        25,
        20,
        'refuge dans la vallée',
        45
    );

INSERT INTO
    Refuge (
        emailRef,
        nomRef,
        sectGeo,
        dateStart,
        dateEnd,
        nbPlaceRepas,
        nbPlaceDormir,
        txtPres,
        prixNuit
    )
VALUES
    (
        'refuge3@example.com',
        'Refuge Vallée',
        'Vallée',
        TO_DATE('2023-01-15', 'YYYY-MM-DD'),
        TO_DATE('2023-12-10', 'YYYY-MM-DD'),
        25,
        15,
        'refuge dans la vallée de la mort',
        30
    );

INSERT INTO
    Refuge (
        emailRef,
        nomRef,
        sectGeo,
        dateStart,
        dateEnd,
        nbPlaceRepas,
        nbPlaceDormir,
        txtPres,
        prixNuit
    )
VALUES
    (
        'refuge5@example.com',
        'Refuge Les collines',
        'Collines',
        TO_DATE('2023-1-05', 'YYYY-MM-DD'),
        TO_DATE('2023-10-15', 'YYYY-MM-DD'),
        35,
        30,
        'beau refuge',
        55
    );

INSERT INTO
    TypePaiement (typePaiement)
VALUES
    ('espèce');

INSERT INTO
    TypePaiement (typePaiement)
VALUES
    ('chèque');

INSERT INTO
    TypePaiement (typePaiement)
VALUES
    ('carte-bleue');

INSERT INTO
    RefugeATypePaiement (emailRef, typePaiement)
VALUES
    ('refuge3@example.com', 'espèce');

INSERT INTO
    RefugeATypePaiement (emailRef, typePaiement)
VALUES
    ('refuge3@example.com', 'carte-bleue');

INSERT INTO
    RefugeATypePaiement (emailRef, typePaiement)
VALUES
    ('refuge4@example.com', 'espèce');

INSERT INTO
    RefugeATypePaiement (emailRef, typePaiement)
VALUES
    ('refuge5@example.com', 'chèque');

COMMIT;

EXCEPTION
WHEN DUP_VAL_ON_INDEX THEN DBMS_OUTPUT.PUT_LINE(
    'Erreur : Une ou plusieurs valeurs dupliquées détectées pour une contrainte d''index unique.'
);

ROLLBACK TO start_point;

WHEN OTHERS THEN v_error_code := SQLCODE;

v_error_msg := SUBSTR(SQLERRM, 1, 100);

DBMS_OUTPUT.PUT_LINE(
    'Erreur rencontrée : ' || v_error_code || ' - ' || v_error_msg
);

ROLLBACK TO start_point;

END;

/ BEGIN SAVEPOINT start_MEMBER;

INSERT INTO
    Membre (
        idUser,
        emailUser,
        MDP,
        nomUser,
        prenomUser,
        adresse
    )
VALUES
    (-1, '/', '/', '/', '/', '/');

INSERT INTO
    Membre (
        idUser,
        emailUser,
        MDP,
        nomUser,
        prenomUser,
        adresse
    )
VALUES
    (
        1,
        'user1@example.com',
        'motdepasse1',
        'Doe',
        'John',
        '123 Street, City'
    );

INSERT INTO
    Membre (
        idUser,
        emailUser,
        MDP,
        nomUser,
        prenomUser,
        adresse
    )
VALUES
    (
        2,
        'user2@example.com',
        'motdepasse2',
        'Smith',
        'Jane',
        '456 Avenue, Town'
    );

INSERT INTO
    Membre (
        idUser,
        emailUser,
        MDP,
        nomUser,
        prenomUser,
        adresse
    )
VALUES
    (
        3,
        'user3@example.com',
        'motdepasse3',
        'Johnson',
        'Alex',
        '789 Road, Village'
    );

INSERT INTO
    Membre (
        idUser,
        emailUser,
        MDP,
        nomUser,
        prenomUser,
        adresse
    )
VALUES
    (
        4,
        'user4@example.com',
        'motdepasse4',
        'Williams',
        'Emily',
        '101 Lane, Countryside'
    );

INSERT INTO
    Membre (
        idUser,
        emailUser,
        MDP,
        nomUser,
        prenomUser,
        adresse
    )
VALUES
    (
        5,
        'user5@example.com',
        'motdepasse5',
        'Brown',
        'Michael',
        '222 Boulevard, Suburb'
    );

COMMIT;

EXCEPTION
WHEN OTHERS THEN ROLLBACK TO start_MEMBER;

DBMS_OUTPUT.PUT_LINE('Erreur rencontrée : ' || SQLERRM);

END;

/ BEGIN SAVEPOINT start_for;

INSERT INTO
    Formation (
        anneeForm,
        rangForm,
        nom,
        dateStart,
        duree,
        nbPlace,
        descFor
    )
VALUES
    (
        2023,
        1,
        'Formation A',
        TO_DATE('2023-11-25', 'YYYY-MM-DD'),
        10,
        20,
        'Formation excellente'
    );

INSERT INTO
    Formation (
        anneeForm,
        rangForm,
        nom,
        dateStart,
        duree,
        nbPlace,
        descFor
    )
VALUES
    (
        2023,
        2,
        'Formation B',
        TO_DATE('2023-12-05', 'YYYY-MM-DD'),
        5,
        15,
        'Formation epique'
    );

INSERT INTO
    Formation (
        anneeForm,
        rangForm,
        nom,
        dateStart,
        duree,
        nbPlace,
        descFor
    )
VALUES
    (
        2023,
        3,
        'Formation C',
        TO_DATE('2023-12-10', 'YYYY-MM-DD'),
        2,
        30,
        'Formation courte et amusante'
    );

COMMIT;

EXCEPTION
WHEN OTHERS THEN ROLLBACK TO start_for;

DBMS_OUTPUT.PUT_LINE('Erreur rencontrée : ' || SQLERRM);

END;

/ BEGIN SAVEPOINT start_act;

INSERT INTO
    Activite (nomActivite)
VALUES
    ('randonnée');

INSERT INTO
    Activite (nomActivite)
VALUES
    ('escalade');

INSERT INTO
    Activite (nomActivite)
VALUES
    ('alpinisme');

INSERT INTO
    Activite (nomActivite)
VALUES
    ('spéléologie');

INSERT INTO
    Activite (nomActivite)
VALUES
    ('ski de rando');

INSERT INTO
    Activite (nomActivite)
VALUES
    ('cascade de glace');

COMMIT;

EXCEPTION
WHEN OTHERS THEN ROLLBACK TO start_act;

DBMS_OUTPUT.PUT_LINE('Erreur rencontrée : ' || SQLERRM);

END;

/ BEGIN SAVEPOINT start_catarb;

INSERT INTO
    ArbreCategorie (categorieFils, categoriePere)
VALUES
    ('epi', 'NULL');

INSERT INTO
    ArbreCategorie (categorieFils, categoriePere)
VALUES
    ('gilet', 'epi');

INSERT INTO
    ArbreCategorie (categorieFils, categoriePere)
VALUES
    ('mousqueton', 'epi');

INSERT INTO
    ArbreCategorie (categorieFils, categoriePere)
VALUES
    ('casque', 'epi');

INSERT INTO
    ArbreCategorie (categorieFils, categoriePere)
VALUES
    ('mousqueton symétrique', 'mousqueton');

INSERT INTO
    ArbreCategorie (categorieFils, categoriePere)
VALUES
    ('mousqueton HMS', 'mousqueton');

INSERT INTO
    ArbreCategorie (categorieFils, categoriePere)
VALUES
    ('gilet vert', 'gilet');

COMMIT;

EXCEPTION
WHEN OTHERS THEN ROLLBACK TO start_catarb;

DBMS_OUTPUT.PUT_LINE('Erreur rencontrée : ' || SQLERRM);

END;

/ BEGIN SAVEPOINT start_lot;

INSERT INTO
    LotMateriel (
        marque,
        modele,
        anneeAchat,
        nbPiece,
        prixAccident,
        categorie
    )
VALUES
    (
        'MarqueA',
        'Modèle1',
        2020,
        5,
        50,
        'mousqueton symétrique'
    );

INSERT INTO
    LotMateriel (
        marque,
        modele,
        anneeAchat,
        nbPiece,
        prixAccident,
        categorie
    )
VALUES
    ('MarqueB', 'ModèleX', 2021, 10, 30, 'gilet vert');

INSERT INTO
    LotMateriel (
        marque,
        modele,
        anneeAchat,
        nbPiece,
        prixAccident,
        categorie
    )
VALUES
    (
        'MarqueC',
        'ModèleZ',
        2019,
        7,
        2,
        'mousqueton HMS'
    );

COMMIT;

EXCEPTION
WHEN OTHERS THEN ROLLBACK TO start_lot;

DBMS_OUTPUT.PUT_LINE('Erreur rencontrée : ' || SQLERRM);

END;

/ BEGIN SAVEPOINT start_mat;

INSERT INTO
    InfoMateriel (infoMat)
VALUES
    ('mousqueton symétrique bonne qualité');

INSERT INTO
    InfoMateriel (infoMat)
VALUES
    ('gilet vert parfait pour les sorties en groupe');

INSERT INTO
    InfoMateriel (infoMat)
VALUES
    ('beau gilet');

COMMIT;

EXCEPTION
WHEN OTHERS THEN ROLLBACK TO start_mat;

DBMS_OUTPUT.PUT_LINE('Erreur rencontrée : ' || SQLERRM);

END;

/ BEGIN SAVEPOINT start_lma;

INSERT INTO
    LotMaterielPourActivites (marque, modele, anneeAchat, nomActivite)
VALUES
    ('MarqueA', 'Modèle1', 2020, 'randonnée');

INSERT INTO
    LotMaterielPourActivites (marque, modele, anneeAchat, nomActivite)
VALUES
    ('MarqueB', 'ModèleX', 2021, 'alpinisme');

INSERT INTO
    LotMaterielPourActivites (marque, modele, anneeAchat, nomActivite)
VALUES
    ('MarqueB', 'ModèleX', 2021, 'randonnée');

INSERT INTO
    LotMaterielPourActivites (marque, modele, anneeAchat, nomActivite)
VALUES
    ('MarqueC', 'ModèleZ', 2019, 'alpinisme');

COMMIT;

EXCEPTION
WHEN OTHERS THEN ROLLBACK TO start_lma;

DBMS_OUTPUT.PUT_LINE('Erreur rencontrée : ' || SQLERRM);

END;

/ BEGIN SAVEPOINT start_FormationPossedeActivites;

INSERT INTO
    FormationPossedeActivites (anneeForm, rangForm, nomActivite)
VALUES
    (2023, 1, 'randonnée');

INSERT INTO
    FormationPossedeActivites (anneeForm, rangForm, nomActivite)
VALUES
    (2023, 1, 'escalade');

INSERT INTO
    FormationPossedeActivites (anneeForm, rangForm, nomActivite)
VALUES
    (2023, 1, 'spéléologie');

INSERT INTO
    FormationPossedeActivites (anneeForm, rangForm, nomActivite)
VALUES
    (2023, 2, 'cascade de glace');

INSERT INTO
    FormationPossedeActivites (anneeForm, rangForm, nomActivite)
VALUES
    (2023, 2, 'ski de rando');

INSERT INTO
    FormationPossedeActivites (anneeForm, rangForm, nomActivite)
VALUES
    (2023, 3, 'alpinisme');

COMMIT;

EXCEPTION
WHEN OTHERS THEN ROLLBACK TO start_FormationPossedeActivites;

DBMS_OUTPUT.PUT_LINE('Erreur rencontrée : ' || SQLERRM);

END;

/ BEGIN SAVEPOINT start_LotMaterielInfo;

INSERT INTO
    LotMaterielInfo (marque, modele, AnneeAchat, infoMat)
VALUES
    (
        'MarqueA',
        'Modèle1',
        2020,
        'mousqueton symétrique bonne qualité'
    );

INSERT INTO
    LotMaterielInfo (marque, modele, AnneeAchat, infoMat)
VALUES
    (
        'MarqueB',
        'ModèleX',
        2021,
        'gilet vert parfait pour les sorties en groupe'
    );

INSERT INTO
    LotMaterielInfo (marque, modele, AnneeAchat, infoMat)
VALUES
    ('MarqueC', 'ModèleZ', 2019, 'beau gilet');

COMMIT;

EXCEPTION
WHEN OTHERS THEN ROLLBACK TO start_LotMaterielInfo;

DBMS_OUTPUT.PUT_LINE('Erreur rencontrée : ' || SQLERRM);

END;

/ BEGIN SAVEPOINT start_PrixDejeuner;

INSERT INTO
    PrixDejeuner (prixDejeuner)
VALUES
    (5);

INSERT INTO
    PrixDejeuner (prixDejeuner)
VALUES
    (8);

INSERT INTO
    PrixDejeuner (prixDejeuner)
VALUES
    (6);

INSERT INTO
    PrixSouper (prixSouper)
VALUES
    (10);

INSERT INTO
    PrixSouper (prixSouper)
VALUES
    (15);

INSERT INTO
    PrixSouper (prixSouper)
VALUES
    (12);

INSERT INTO
    PrixCasseCroute (prixCasseCroute)
VALUES
    (3);

INSERT INTO
    PrixCasseCroute (prixCasseCroute)
VALUES
    (4);

INSERT INTO
    PrixCasseCroute (prixCasseCroute)
VALUES
    (5);

INSERT INTO
    PrixDiner (prixDiner)
VALUES
    (12);

INSERT INTO
    PrixDiner (prixDiner)
VALUES
    (18);

INSERT INTO
    PrixDiner (prixDiner)
VALUES
    (15);

COMMIT;

EXCEPTION
WHEN OTHERS THEN ROLLBACK TO start_PrixDejeuner;

DBMS_OUTPUT.PUT_LINE('Erreur rencontrée : ' || SQLERRM);

END;

/ BEGIN SAVEPOINT start_RefugeAPrixDej;

INSERT INTO
    RefugeAPrixDejeuner (emailRef, prixDejeuner)
VALUES
    ('refuge3@example.com', 5);

INSERT INTO
    RefugeAPrixDejeuner (emailRef, prixDejeuner)
VALUES
    ('refuge4@example.com', 5);

INSERT INTO
    RefugeAPrixDejeuner (emailRef, prixDejeuner)
VALUES
    ('refuge5@example.com', 6);

INSERT INTO
    RefugeAPrixSouper (emailRef, prixSouper)
VALUES
    ('refuge3@example.com', 10);

INSERT INTO
    RefugeAPrixSouper (emailRef, prixSouper)
VALUES
    ('refuge4@example.com', 12);

INSERT INTO
    RefugeAPrixSouper (emailRef, prixSouper)
VALUES
    ('refuge5@example.com', 15);

INSERT INTO
    RefugeAPrixDiner (emailRef, prixDiner)
VALUES
    ('refuge3@example.com', 12);

INSERT INTO
    RefugeAPrixDiner (emailRef, prixDiner)
VALUES
    ('refuge4@example.com', 15);

INSERT INTO
    RefugeAPrixDiner (emailRef, prixDiner)
VALUES
    ('refuge5@example.com', 18);

INSERT INTO
    RefugeAPrixCasseCroute (emailRef, prixCasseCroute)
VALUES
    ('refuge3@example.com', 3);

INSERT INTO
    RefugeAPrixCasseCroute (emailRef, prixCasseCroute)
VALUES
    ('refuge4@example.com', 5);

INSERT INTO
    RefugeAPrixCasseCroute (emailRef, prixCasseCroute)
VALUES
    ('refuge5@example.com', 4);

COMMIT;

EXCEPTION
WHEN OTHERS THEN ROLLBACK TO start_RefugeAPrixDej;

DBMS_OUTPUT.PUT_LINE('Erreur rencontrée : ' || SQLERRM);

END;

/ BEGIN SAVEPOINT start_tel;

INSERT INTO
    Telephone (numTel, emailRef)
VALUES
    ('0663122578', 'refuge3@example.com');

INSERT INTO
    Telephone (numTel, emailRef)
VALUES
    ('0651892014', 'refuge5@example.com');

COMMIT;

EXCEPTION
WHEN OTHERS THEN ROLLBACK TO start_tel;

DBMS_OUTPUT.PUT_LINE('Erreur rencontrée : ' || SQLERRM);

END;

/ BEGIN SAVEPOINT start_resrefuge;

INSERT INTO
    ReservationRefuge (
        idRR,
        dateRR,
        heure,
        nbNuits,
        nbRepas,
        prixRes,
        idUser,
        emailRef
    )
VALUES
    (
        1,
        TO_DATE('2023-05-01', 'YYYY-MM-DD'),
        10,
        3,
        2,
        150,
        1,
        'refuge4@example.com'
    );

INSERT INTO
    ReservationRefuge (
        idRR,
        dateRR,
        heure,
        nbNuits,
        nbRepas,
        prixRes,
        idUser,
        emailRef
    )
VALUES
    (
        2,
        TO_DATE('2023-04-01', 'YYYY-MM-DD'),
        12,
        2,
        1,
        100,
        2,
        'refuge4@example.com'
    );

INSERT INTO
    ReservationRefuge (
        idRR,
        dateRR,
        heure,
        nbNuits,
        nbRepas,
        prixRes,
        idUser,
        emailRef
    )
VALUES
    (
        3,
        TO_DATE('2023-04-21', 'YYYY-MM-DD'),
        15,
        4,
        3,
        220,
        3,
        'refuge5@example.com'
    );

COMMIT;

EXCEPTION
WHEN OTHERS THEN ROLLBACK TO start_resrefuge;

DBMS_OUTPUT.PUT_LINE('Erreur rencontrée : ' || SQLERRM);

END;

/ BEGIN SAVEPOINT start_repasres;

INSERT INTO
    RepasReserve (
        idRR,
        jour,
        aReserveDej,
        aReserveDiner,
        aReserveSoup,
        aReserveCC
    )
VALUES
    (
        1,
        TO_DATE('2023-12-01', 'YYYY-MM-DD'),
        'Oui',
        'Non',
        'Oui',
        'Non'
    );

INSERT INTO
    RepasReserve (
        idRR,
        jour,
        aReserveDej,
        aReserveDiner,
        aReserveSoup,
        aReserveCC
    )
VALUES
    (
        2,
        TO_DATE('2023-7-01', 'YYYY-MM-DD'),
        'Non',
        'Oui',
        'Oui',
        'Non'
    );

INSERT INTO
    RepasReserve (
        idRR,
        jour,
        aReserveDej,
        aReserveDiner,
        aReserveSoup,
        aReserveCC
    )
VALUES
    (
        3,
        TO_DATE('2024-04-21', 'YYYY-MM-DD'),
        'Oui',
        'Oui',
        'Oui',
        'Oui'
    );

COMMIT;

EXCEPTION
WHEN OTHERS THEN ROLLBACK TO start_repasres;

DBMS_OUTPUT.PUT_LINE('Erreur rencontrée : ' || SQLERRM);

END;

/ BEGIN SAVEPOINT start_adh;

INSERT INTO
    Adherent (
        idAdherent,
        idUser,
        emailUser,
        MDP,
        nomUser,
        prenomUser,
        adresse
    )
VALUES
    (-1, -1, '/', '/', '/', '/', '/');

INSERT INTO
    Adherent (
        idAdherent,
        idUser,
        emailUser,
        MDP,
        nomUser,
        prenomUser,
        adresse
    )
VALUES
    (
        1,
        1,
        'user1@example.com',
        'motdepasse1',
        'Doe',
        'John',
        '123 Street, City'
    );

INSERT INTO
    Adherent (
        idAdherent,
        idUser,
        emailUser,
        MDP,
        nomUser,
        prenomUser,
        adresse
    )
VALUES
    (
        2,
        2,
        'user2@example.com',
        'motdepasse2',
        'Smith',
        'Jane',
        '456 Avenue, Town'
    );

COMMIT;

EXCEPTION
WHEN OTHERS THEN ROLLBACK TO start_adh;

DBMS_OUTPUT.PUT_LINE('Erreur rencontrée : ' || SQLERRM);

END;

/ BEGIN SAVEPOINT start_RangListeAttente;

INSERT INTO
    RangListeAttente (rangLA)
VALUES
    (1);

INSERT INTO
    RangListeAttente (rangLA)
VALUES
    (2);

INSERT INTO
    RangListeAttente (rangLA)
VALUES
    (3);

INSERT INTO
    RangListeAttente (rangLA)
VALUES
    (4);

INSERT INTO
    RangListeAttente (rangLA)
VALUES
    (5);

INSERT INTO
    RangListeAttente (rangLA)
VALUES
    (6);

INSERT INTO
    RangListeAttente (rangLA)
VALUES
    (7);

INSERT INTO
    RangListeAttente (rangLA)
VALUES
    (8);

INSERT INTO
    RangListeAttente (rangLA)
VALUES
    (9);

INSERT INTO
    RangListeAttente (rangLA)
VALUES
    (10);

INSERT INTO
    RangListeAttente (rangLA)
VALUES
    (11);

INSERT INTO
    RangListeAttente (rangLA)
VALUES
    (12);

INSERT INTO
    RangListeAttente (rangLA)
VALUES
    (13);

INSERT INTO
    RangListeAttente (rangLA)
VALUES
    (14);

INSERT INTO
    RangListeAttente (rangLA)
VALUES
    (15);

INSERT INTO
    RangListeAttente (rangLA)
VALUES
    (16);

INSERT INTO
    RangListeAttente (rangLA)
VALUES
    (17);

INSERT INTO
    RangListeAttente (rangLA)
VALUES
    (18);

INSERT INTO
    RangListeAttente (rangLA)
VALUES
    (19);

INSERT INTO
    RangListeAttente (rangLA)
VALUES
    (20);

COMMIT;

EXCEPTION
WHEN OTHERS THEN ROLLBACK TO start_RangListeAttente;

DBMS_OUTPUT.PUT_LINE('Erreur rencontrée : ' || SQLERRM);

END;

/ BEGIN SAVEPOINT start_ReservationFormation;

INSERT INTO
    ReservationFormation (
        idRF,
        inscrit,
        idAdherent,
        idUser,
        anneeForm,
        rangForm
    )
VALUES
    (1, 'validé', 1, 1, 2023, 1);

INSERT INTO
    ReservationFormation (
        idRF,
        inscrit,
        idAdherent,
        idUser,
        anneeForm,
        rangForm
    )
VALUES
(2, 'attente', 2, 2, 2023, 2);

COMMIT;

EXCEPTION
WHEN OTHERS THEN ROLLBACK TO start_ReservationFormation;

DBMS_OUTPUT.PUT_LINE('Erreur rencontrée : ' || SQLERRM);

END;

/ BEGIN SAVEPOINT start_RangAttenteReservationForm;

INSERT INTO
    RangAttenteReservationForm (idRF, rangLA)
VALUES
    (2, 5);

COMMIT;

EXCEPTION
WHEN OTHERS THEN ROLLBACK TO start_RangAttenteReservationForm;

DBMS_OUTPUT.PUT_LINE('Erreur rencontrée : ' || SQLERRM);

END;

/ BEGIN SAVEPOINT start_LocationMateriel;

INSERT INTO
    LocationMateriel (
        idLM,
        nbPieceR,
        dateRecup,
        dateRetour,
        idAdherent,
        idUser,
        marque,
        modele,
        anneeAchat
    )
VALUES
    (
        1,
        2,
        TO_DATE('2023-11-24', 'YYYY-MM-DD'),
        TO_DATE('2023-11-28', 'YYYY-MM-DD'),
        1,
        1,
        'MarqueA',
        'Modèle1',
        2020
    );

INSERT INTO
    LocationMateriel (
        idLM,
        nbPieceR,
        dateRecup,
        dateRetour,
        idAdherent,
        idUser,
        marque,
        modele,
        anneeAchat
    )
VALUES
    (
        2,
        5,
        TO_DATE('2023-12-05', 'YYYY-MM-DD'),
        TO_DATE('2023-12-10', 'YYYY-MM-DD'),
        2,
        2,
        'MarqueB',
        'ModèleX',
        2021
    );

COMMIT;

EXCEPTION
WHEN OTHERS THEN ROLLBACK TO start_LocationMateriel;

DBMS_OUTPUT.PUT_LINE('Erreur rencontrée : ' || SQLERRM);

END;

/ BEGIN SAVEPOINT start_NbPieceAccident;

INSERT INTO
    NbPieceAccident (nb)
VALUES
    (0);

INSERT INTO
    NbPieceAccident (nb)
VALUES
    (1);

INSERT INTO
    NbPieceAccident (nb)
VALUES
    (2);

INSERT INTO
    NbPieceAccident (nb)
VALUES
    (3);

INSERT INTO
    NbPieceAccident (nb)
VALUES
    (4);

INSERT INTO
    NbPieceAccident (nb)
VALUES
    (5);

INSERT INTO
    NbPieceAccident (nb)
VALUES
    (6);

INSERT INTO
    NbPieceAccident (nb)
VALUES
    (7);

INSERT INTO
    NbPieceAccident (nb)
VALUES
    (8);

INSERT INTO
    NbPieceAccident (nb)
VALUES
    (9);

INSERT INTO
    NbPieceAccident (nb)
VALUES
    (10);

-- INSERT INTO NbPieceAccidentLocMat (idLM, nb) VALUES (1, 0);
-- INSERT INTO NbPieceAccidentLocMat (idLM, nb) VALUES (2, 2);
COMMIT;

EXCEPTION
WHEN OTHERS THEN ROLLBACK TO start_NbPieceAccident;

DBMS_OUTPUT.PUT_LINE('Erreur rencontrée : ' || SQLERRM);

END;

/ BEGIN SAVEPOINT start_Somme;

INSERT INTO
    Somme (typeSO)
VALUES
    ('coûtsREF');

INSERT INTO
    Somme (typeSO)
VALUES
    ('coûtsFOR');

INSERT INTO
    Somme (typeSO)
VALUES
    ('sommeAccident');

INSERT INTO
    Somme (typeSO)
VALUES
    ('sommeRemboursé');

INSERT INTO
    DescriptionSomme (descSO)
VALUES
    (
        'le coût totale de toutes les reservations de refuge'
    );

INSERT INTO
    DescriptionSomme (descSO)
VALUES
    (
        'le coût totale de toutes les reservations de formation'
    );

INSERT INTO
    DescriptionSomme (descSO)
VALUES
    (
        'somme due à causes des matériels abimés lors des locations'
    );

INSERT INTO
    DescriptionSomme (descSO)
VALUES
    ('somme dèjà remboursé par le client');

INSERT INTO
    SommeAPourDescription (TypeSO, descSO)
VALUES
    (
        'coûtsREF',
        'le coût totale de toutes les reservations de refuge'
    );

INSERT INTO
    SommeAPourDescription (TypeSO, descSO)
VALUES
    (
        'coûtsFOR',
        'le coût totale de toutes les reservations de formation'
    );

INSERT INTO
    SommeAPourDescription (TypeSO, descSO)
VALUES
    (
        'sommeAccident',
        'somme due à causes des matériels abimés lors des locations'
    );

INSERT INTO
    SommeAPourDescription (TypeSO, descSO)
VALUES
    (
        'sommeRemboursé',
        'somme dèjà remboursé par le client'
    );

INSERT INTO
    SommeDueUser (idUser, TypeSO, prix)
VALUES
    (1, 'coûtsREF', 100);

INSERT INTO
    SommeDueUser (idUser, TypeSO, prix)
VALUES
    (1, 'coûtsFOR', 150);

INSERT INTO
    SommeDueUser (idUser, TypeSO, prix)
VALUES
    (2, 'coûtsREF', 150);

INSERT INTO
    SommeDueUser (idUser, TypeSO, prix)
VALUES
    (2, 'coûtsFOR', 70);

INSERT INTO
    SommeDueUser (idUser, TypeSO, prix)
VALUES
    (2, 'sommeAccident', 20);

INSERT INTO
    SommeDueUser (idUser, TypeSO, prix)
VALUES
    (2, 'sommeRemboursé', 50);

INSERT INTO
    SommeDueUser (idUser, TypeSO, prix)
VALUES
    (3, 'coûtsREF', 42);

COMMIT;

EXCEPTION
WHEN OTHERS THEN ROLLBACK TO start_Somme;

DBMS_OUTPUT.PUT_LINE('Erreur rencontrée : ' || SQLERRM);

END;