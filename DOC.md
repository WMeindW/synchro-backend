# Synchro

- název: Synchro
- verze 0.86
- moduly:
    - správa a vytváření zaměstnanců
    - plánování událostí a směn
    - motd (message-of-the-day)
    - check-in/check-out
    - správa událostí
    - admin a user interface
    - stručné statistiky docházky a plánů zaměstnanců
    - vlastní uložiště souborů pro každého uživatele

---

- Workflow je verzovaný na GitHub repozitářích.
  Jako backend framework jsem zvolil **Spring-Boot**, jako nejlepší enterprise řešení mých problémů.
  Díky potřebě rychle měnících se stavů a hodnot na frontend prostředí byl zvolen **React**, jelikož se jedná o
  nejvhodnější framework pro tyto parametry.

---

- Postup na projektu začal v říjnu po extenzivním plánování. Před začátkem veškeré práce, jsem vytvořil návrhy, palety
  barev a diagramy workflow celého programu. První byl napsán celý backend a funkčnosti s malým lightweight ui v reactu
  na testování. Velký problém nastal při výběru knihoven pro zobrazování složitějších dat, jako jsou statistiky a
  události, jelikož bylo nutné přizpůsobit formát dat pro tyto závislosti. Prošel jsem několika pokusy o integraci
  externích knihoven a až pátý splňoval mé požadavky. Tento proces zabral spoustu času mého jednočleného týmu.
  Další zádrhely přišly při testování a odstraňování chyb, zejména těch skrytých ovlivňujících zejména user experience.

---

- Tento projekt není výdělečný a jedná se jen a pouze o studentskou práci. Cena tohoto projektu se dá počítat v
  zaplacených technologiich a tzv. `Man-Hour`. Pro tuto práci byl potřeba development server, který zřizuje nasazení
  databáze, java runtime a nginx reverse-proxy. Nakonec jsem se rozhodl využít služeb **Oracle development cloud**. Tato
  služba mi poskytuje Linux prostředí v podobě vzdáleného serveru. Zde jsem využil svoje *supreme* schopnosti s *Linux
  OS* a nastavil celé prostředí pro runtime Javy, MySQL, Nginx a konfigurace *https* certifikátů a autorit.

## Competition

- existuje přímá konkurence v podobě aplikace **Tamigo**
- toto nneí důležité, jelikož tato aplikace je open-source a přehlednější na používání pro bězného uživatele

## Links

- [Synchro](https://daniellinda.net)
    - [Login](https://daniellinda.net/synchro/api/auth/index.html)

- [Contact](https://daniellinda.net/linktree/)

## Contribute

- contribute at:
    - [Backend](https://github.com/WMeindW/synchro-backend)
    - [Frontend](https://github.com/WMeindW/synchro-react)

## Employee management software

- free opensource software pro malé a začínající podniky
- vhodné pro subjekty s počtem zaměstnanců menším než **50**
- software je modulární a dovoluje snadné rozšíření o další moduly

## Prerequisites

- java 17+
- relační databáze (specifikovaná v konfiguraci)
- nginx/apache

## Deployment

- nasazení je velice jednoduché
- stačí přetáhnout požadované soubory, nastavit nginx a zapnout backend
- základní cesta na backend je `/synchro/api/`
- základní cesta k statickým souborům je `/synchro/`
- software vyžaduje doménu s platným https certifikátem

## Economics

- software je `open-source` v čistě vzdělávacím scénáři
- lze použít na trhu

## Configuration

- ukázková konfigurace:

```properties
server.port=8083
spring.jpa.properties.hibernate.cache.use_second_level_cache=true
spring.jpa.properties.hibernate.cache.region.factory_class=org.hibernate.cache.ehcache.EhCacheRegionFactory
logging.level.org.hibernate.cache=DEBUG
logging.level.net.sf.ehcache=DEBUG
logging.level.org.ehcache=DEBUG
logging.file.path=service.log
logging.level.web=DEBUG
spring.jpa.hibernate.ddl-auto=update
spring.datasource.url=jdbc:mysql://localhost:3306/synchro
spring.datasource.username=root
spring.datasource.password=[passwd]
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
spring.jpa.hibernate.naming.physical-strategy=org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
spring.jpa.open-in-view=true
security.jwt.secret-key=[jwt_passwd]
security.jwt.expiration-time=3600000
security.jwt.default-role=USER
security.jwt.admin-role=ADMIN
security.jwt.combined-role=ADMIN/USER
security.jwt.admin-password=[passwd]
security.jwt.admin-username=admin_user
security.jwt.secure-route=src/main/resources/secure
security.jwt.signup-link-expires=36000000
security.jwt.host-address=http://localhost:8083/
security.jwt.login-page=/auth/login.html
security.jwt.user-dashboard-page=/user/index.html
events.synchro.types=SHIFT,VACATION,HOME-OFFICE-SHIFT,SICK-LEAVE
attendance.synchro.type=WORK
files.synchro.location=src/main/resources/files
files.synchro.max-size.bites=100000000
spring.servlet.multipart.max-file-size=100MB
spring.servlet.multipart.max-request-size=100MB
```

## License

Copyright 2025 [Daniel Linda](https://daniellinda.net/linktree/)

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
documentation files (the “Software”), to deal in the Software without restriction, including without limitation the
rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit
persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.