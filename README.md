</p>
  <p>
    <tt>
      oppna-program-ifeed
    </tt>
     Är en del i Västra Götalandsregionens satsning på Öppen källkod inom ramen för 
    <a href="https://github.com/Vastra-Gotalandsregionen//oppna-program">
      Öppna Program
    </a>
    . 
  </p>


De vanliga användarna kommer i kontakt med IFeed oftast utan att veta om det. Typiskt går man in på vårt intranät - eller någon annan sida (internt eller externt) och ser där en lista på dokument som tidigare publicerats i något av dokumenthanteringssystem (Alfresco, Barium eller SOFIA). Dessa listor har länkar som gör det möjligt att tanka hem och öppna dokumenten i sig och att granska metadata som dessa försetts med i källsystemet. 
Användaren har inte navigerat till IFeed-systemet utan det är den sida som denne befinner sig på som har läst in data ifrån Ifeed. Det initieras av att sidan läser in ett skript in ifrån ifeed (iFeed-web-script) via Varnish. Detta skript körs sedan på den sida som användaren nyss navigerade till. Det som skriptet gör är att den söker efter IFeed-taggar, och i dem id:n, i källkoden på den befintliga sidan och sedan gör ett anrop till ifeed för att hämta den data, det flöde - som det kallas, som associerats ihop med just det id:t. Detta görs via ett bakgrundsanrop som vanligen är så kvickt att det inte uppfattas. 
Som komplement till den här skriptinläsningen finns också visningslogik konstruerad i Epi-server-block. Denna funktionalitet gör då samma sak som skriptet på sidan fast inne i Epi:s server. Den här delen är inte en explicit del av IFeed även om den använder datahämtningstjänsten ifrån denna. 
När ifeed, komponenten iFeed-web, får ett anrop där ett visst flöde efterfrågas händer två saker: 
-	Först hämtar den sökvillkor, matchningar av värden mot dokumentmetadata, ifrån databasen.
-	Sedan anropar den vårt dokumentsökindex med dessa villkor och får på så vis fram en träfflista. Detta resultat skickas sedan tillbaka som svar. 
Det är sedan skriptet, som kommer ifrån komponenten iFeed-web-script, som tar hand om resultatet på klientsidan och där bygger en synlig lista med länkar och information om dokumentens metadatavärden.
Ifeed-admin
För att göra listor av dokument som användarna ska kunna se ute på webbsidorna så kan IFeed-administratörer konstruera dessa s.k. flöden. Detta gör de i verktyget IFeed-admin som ligger konstruerad som en portlet i vår uppsättning av portal-servern Liferay. Där kan man konstruera sökfrågorna som ger upphov till den data som sedan visas. Dessa indexfrågor sparas sedan i databasen så att den tidigare nämnda delen av applikationen kan använda den. 
I administrationsverktyget, iFeed-admin, kan man sedan skapa små kodsnuttar som beskriver vilket flöde det kommer ifrån (via tidigare nämnda id) samt vilka kolumner som ska finnas med och hur de ska sorteras. Detta tar man sedan med sig till den sida där man vill visa flödet i fråga. Man klistrar in den koden i sidans kod. När användaren sedan går in på sidan skannar skriptet den och hittar de här koderna, som beskrivet tidigare.
Integrationer
För att komplettera dokumentens metadatavärden ansluter IFeed till diverse andra datakällor. För att kunna göra sökningar på vgr:s organisationsdelar så hämtas deras namn ifrån KIV. För att kunna göra sökningar på personer, ansvariga för dokument mm, så hämtas information ifrån VGR AD. Data ifrån vgr:s metadatatjänst (Apelon) hämtas för att underlätta att för administratören att veta vilka termer som finns, eller skulle kunna finnas, i vissa av metadatafälten. Liferay, vari iFeed-admin ligger, använder också VGR AD:t för att validera administratörens identitet.  
