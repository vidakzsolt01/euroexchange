# euroexchange
Kompetenciafelméréshez feladat

Az ECB honlapjáról letöltött árfolyam-XML szerinti devizaátszámítást végez.<br><br>
A program alapértelmezetten a https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml 
URL mögött talált file alapján végzi a számításokat oly módon, hogy a file-t letölti 
a bázis alkönyvtár ./xml alkönyvtárába, s innen felolvasva dolgozza fel.<br>
Ha nem sikerül letölteni az előbbi URL-ről az XML-file-t, akkor megőróbál megnyitni 
egy alapértelmezettként a programmal szállított minta XML-t (arfolyam_20220127_080000.xml). 
Ha ez sem sikerül, akkor tájékoztató üzenet után várakozik, hogy a felhasználó  indítsa a file-tallózást<br><br>
A <i>"Forrás"</i> rádiógombok használatával lehet választani, hogy a fenti honlapról aktuálisan letöltött file (<i>"Aktuális (online)"</i>) vagy korábbi indítsok során lementett file-ok közül kiválasztott (<i>"Archív (file-ból)"</i>) alapán történjék az átváltás.<br><br>
Fileletöltés:<br>
 - a program indulásakor ellenőrzi, hogy van-e a korábban letöltött file-ok közt 
olyan, amelyiket az indítás órájában már letöltött egyszer. Ha van ilyen, akkor nem tölti le újabb file-t<br>
- minden további letöltés előtt újra vizsgálja, hogy az utolsó letöltés óta eltel-e már egy óra, s ameddig ez nem következik be, nem tölt le újabb file-t<br>

File-tallózás:<br>
 - az induló alkönyvtár a bázis alkönyvtár ./xml alkönyvtára<br>
 - választható filenév-szűrő az "Árfolyam XML-ek, amely illeszkedik az "arfolyam_ÉÉHHNN_ÓÓPPMM.xml" 
mintára. Miután azonban az <i>"All files"</i> nem kerülhető el a file-browserben, a kiválasztott filenevet a program ellenőrzi és nem megfelelés esetén - üzenet mellett - meg sem kísérli a file betöltését<br>

A használathoz: nincs külön "Számítás" v. "Átváltás" v. efféle gomb, a program egyszerűen a vezérlők eseményei szerint számol, vagyis, ha megváltozik
- a <i>"Devizaérték"</i> mező értéke vagy
- a <i>"Devizák"</i> lista kiválaztott eleme,
akkor lefut a számítás. (Figyelem! A szövegmező <i>megváltozását</i> egy (szám)billentyűletütés <b>nem</b> váltja ki, csak a mezőn <b>leütött [Enter]</b> billentyű.)
