# Web Application Aste Online - Progetto TIW 2022/23

*You can read this also in [English](README.md)*

> **Nota Accademica:** Questo progetto è stato sviluppato per il corso di *Tecnologie Informatiche per il Web* del Politecnico di Milano (Anno Accademico 2022/23), ottenendo una valutazione finale di **27/30**.

### Project Description
Il progetto implementa una piattaforma di gestione per aste online sviluppata in due versioni distinte e complete di applicazione web:

* *Versione Pure HTML*: Architettura classica lato server basata su Java Servlets, JSPs e JDBC, in cui ogni interazione e cambio di stato comporta il ricaricamento completo della pagina.

* *Versione JavaScript / RIA (Rich Internet Application)*: Architettura Single Page Application (SPA) che estende le specifiche precedenti gestendo l'intera interfaccia asincronamente tramite chiamate al server (AJAX) e persistenza lato client senza ricaricamenti.

### Funzionalità e Requisiti Principali
* **Autenticazione e Sicurezza:** Gestione degli accessi tramite login con controlli di validità dei parametri rigorosi sia lato client che lato server.
* **Modulo Vendita (VENDO):** Permette agli utenti di inserire nuovi articoli (codice, nome, descrizione, immagine, prezzo) e creare aste specificando prezzo iniziale, rialzo minimo e scadenza. Include la visualizzazione delle proprie aste (aperte/chiuse) e la possibilità di chiudere quelle scadute.
* **Modulo Acquisto (ACQUISTO):** Offre una form di ricerca per parole chiave tra le aste aperte, pagine di dettaglio con lo storico delle offerte ordinate per data decrescente e un sistema di inserimento offerte vincolato al rialzo minimo. Mostra inoltre l'elenco delle aste aggiudicate all'utente.
* **Persistenza RIA (Versione JS):** Gestisce la navigazione tramite un'unica pagina salvando l'ultima azione compiuta e le aste visitate di recente per la durata di un mese tramite meccanismi client-side.

### Architettura e Tecnologie
* **Backend:** Java EE (Servlets, JavaBeans, pattern DAO, Filtri di sicurezza).
* **Frontend (Pure HTML):** HTML5, CSS3, JSP (JavaServer Pages).
* **Frontend (RIA):** JavaScript nativo, manipolazione dinamica del DOM, comunicazione asincrona.
* **Ambiente di Sviluppo & Server:** Eclipse IDE (Dynamic Web Project structure), Apache Tomcat.


### Repository Structure
`docs/`: Include le relazioni del progetto e le specifiche/regole originali.

`pure-html/`: Contiene l'applicazione Java EE multipagina completa (Servlet, DAO, Bean e JSP) basata sul rendering lato server.

`javascript/`: Contiene la versione Single Page Application (SPA) che sfrutta interazioni asincrone AJAX/Fetch e la gestione dello stato lato client.