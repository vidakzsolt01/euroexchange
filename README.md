# euroexchange
Kompetenciafelméréshez feladat

Az ECB honlapjáról letöltött árfolyam-XML szerinti devizaátszámítást végez.<br><br>
A program alapértelmezetten a https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml 
URL mögött talált file alapján végzi a számításokat oly módon, hogy a file-t letölti 
a bázis alkönyvtár ./xml alkönyvtárába, s innen felolvasva dolgozza fel.<br>
Ha nem sikerül letölteni az előbbi URL-ről az XML-file-t, akkor megpróbál megnyitni 
egy alapértelmezettként a programmal szállított minta XML-t (arfolyam_20220127_080000.xml). 
Ha ez sem sikerül, akkor - tájékoztató üzenet után - várakozik, hogy a felhasználó a file-tallózóval kiválasszon egy betölthető XML-t.<br><br>
A <i>"Forrás"</i> rádiógombok használatával lehet választani, hogy a fenti honlapról aktuálisan letöltött file (<i>"Aktuális (online)"</i>) vagy korábbi indítások során lementettek közül kiválasztott vagy tetszőleges XML file (<i>"Archív (file-ból)"</i>) alapán történjék az átváltás.<br><br>
File-letöltés:<br>
 - a program indulásakor ellenőrzi, hogy van-e a korábban letöltött file-ok közt 
olyan, amelyik az aktuális gépidő órájában már letöltésre került. Ha van ilyen, akkor nem tölt le újabb file-t,
- minden további letöltési igény (l: <i>"Aktuális"</i> rádiógom kiválasztása) előtt újra vizsgálja, hogy az utolsó letöltés óta eltelt-e már egy óra, s ameddig ez nem következik be, nem tölt le újabb file-t.<br>

File-tallózás:<br>
 - az induló alkönyvtár a bázis alkönyvtár ./xml alkönyvtára<br>
 - választható filenév-szűrő az "Árfolyam XML-ek", amelyek illeszkednek az "arfolyam_ÉÉHHNN_ÓÓPPMM.xml" mintára vagy bármilyen nevű XML. Miután azonban az <i>"All files"</i> nem kerülhető el a file-browserben, és a "bármely XML" szűrő szerint is akár tetszőleges XML-t ki lehet választani, a program ellenőrzi, hogy
   - ténylegesen XML-t, és a
   - betöltés során pedig azt, hogy ECB-s árfolym-XML-t 
választott a felhasználó.<br>

A használathoz: nincs külön "Számítás" v. "Átváltás" v. efféle gomb, a program egyszerűen a vezérlelemek változásai nyomán számol, vagyis, ha megváltozik
- a <i>"Devizaérték"</i> mező értéke vagy
- a <i>"Devizanemek"</i> lista kiválaztott eleme,
akkor lefut a számítás. (Figyelem! A szövegmező <i>megváltozását</i> egy (szám)billentyűletütés <b>nem</b> váltja ki, csak a mezőn <b>leütött [Enter]</b> billentyű.)


<b>2022. 02. 11.</b> <b>Vidák Zsolt</b>
