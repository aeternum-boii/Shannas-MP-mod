# Changelogs

This file establishes the authoritative changelog for the project beginning with v4.0.0. 
There have been tries to establish proper changelogs before but those usually were not exhaustive. Those can also be found here, but do not always show everything that has changed.

Versions contained in this file (descending):
 - [v4.0.0](#v400--back-to-top) *not yet released*
 - [v3.25.1](#v3251--back-to-top) *(06.10.2026)*
 - [v3.24.3](#v3243--back-to-top) *(03.10.2026)*
 - [v3.24.2](#v3242--back-to-top) *(26.09.2026)* (sic!)
 - [v3.24.0](#v3240--back-to-top) *(26.09.2026)*


## v3.24.0 <small>[? Back to top](#changelogs)</small>
#### Cumulative changelog vs. vanilla Europa Universalis IV

> **Historical baseline:** This section is a reconstructed comparison of the
> Live branch at patch 3.24 against vanilla EU4. It is not an exhaustive
> record of changes introduced specifically in v3.24. nor does it cover all changes compared to vanilla

**Branch:** `Live`  
**Patch:** `3.24`  
**Git commit:** `5e2908ce277c047279b62a3144e979dc86d18de0`

This is a **current-state comparison against vanilla EU4**, not a version-by-version history. It describes the gameplay differences present on the `Live` branch at patch 3.24.

---

### 1. Core game rules

The mod changes a large number of global defines that affect warfare, diplomacy, economy and long campaigns.

##### Campaign and country rules
- End date extended to **31 December 1835**.
- Monarch power cap increased to **2000**.
- Maximum war exhaustion increased to **50**.
- Abandoning an idea group refunds **80%** of its cost.
- Female generals are always enabled.
- Female advisor chance increased to **35%**.
- Rulers can gain up to **4 additional personalities**, with extra personality checks every **7 years**.
- Artillery and naval barrages cost **25 military power**.
- Culture conversion base power cost reduced to **25**.
- Claim decay and core-loss timings are altered.
- The game supports up to **100 colonial nations**.

##### Diplomacy
- **10 Great Powers**.
- Up to **13 Free Cities**.
- Base truce length reduced to **2 years**, with scaled truces reaching **13 years**.
- Alliance formation is allowed down to **-100 relations**.
- Breaking an alliance has no forced cooldown penalty.
- Base diplomatic vassalisation development cap increased to **200**.
- War reparations are **15%**.
- Aggressive-expansion calculations are substantially altered, including reduced HRE-internal AE, reduced same-religion-group AE, reduced province-demand AE, reduced return-core AE, and a province AE cap of 30.

##### Government and administration
- Expand Administration and Centralize State costs/timings are changed.
- Centralizing a state takes **10 years**.
- AI tolerance for exceeding governing capacity is changed.

---

### 2. Combat and military rules

Combat is more terrain-sensitive and generally more punishing.

##### Global combat changes
- Armies cannot retreat for the first **8 days** of combat.
- Siege phases last **25 days**.
- Besieging forts requires **2×** the normal fort-level manpower multiplier.
- Crossing penalties are increased:
  - River: **-2**
  - Strait: **-3**
  - Naval landing: **-4**
- Long wars increase battle damage through a custom war-length damage modifier.
- Army drill provides up to **10% morale**.
- Leader generation and leader-power rules are altered.
- Supply-limit attrition is stronger.

##### Terrain
Terrain has been heavily rewritten.

Examples:
- **Forest:** +1 defender roll, -20% combat width.
- **Hills:** +1 defender roll, -25% combat width.
- **Woods:** +1 defender roll, -20% combat width.
- **Mountains:** +2 defender rolls, -75% combat width, +1 attrition.
- **Jungle:** +1 defender roll, -25% combat width, +0.5 attrition.
- **Marsh:** +2 defender rolls, -25% combat width.
- **Highlands:** +1 defender roll, -50% combat width.
- Farmlands/grasslands/savannah are substantially easier to supply than rough terrain.


---

### 3. Army professionalism

Army Professionalism milestones are considerably stronger than vanilla.

- **20%:** Supply Depots.
- **40%:** Refill Garrison.
- **60%:** Return manpower when disbanding.
- **80%:** +50% reserves organisation and +1 free leader.
- **100%:** -50% general cost, +5% discipline, +100% drill gain and +1 free leader.

---

### 4. Hegemons

All three hegemon types are reworked.

##### Economic Hegemon
Normally requires **1000 monthly income** and Great Power income supremacy.

Base:
- -0.10 monthly war exhaustion
- -5% development cost modifier
- +100% mercenary manpower

Scaling:
- +10% mercenary discipline
- +25% global goods-produced size
- -20% minimum autonomy in territories

Maximum:
- +20% governing capacity

##### Naval Hegemon
Normally requires **250 ships** and naval superiority among Great Powers.

Base:
- +20% ship trade power
- -0.10 monthly war exhaustion
- +10% naval engagement width modifier

Scaling:
- -20 subject liberty desire
- +200% sailors
- +15% heavy-ship combat ability

Maximum:
- +20% back-row artillery damage

##### Military Hegemon
Normally requires **500 regiments**.

Base:
- +1 monthly military power
- -0.10 monthly war exhaustion
- -10% province warscore cost

Scaling:
- +0.25 flat land morale
- -25% land attrition
- +25% reserves organisation

Maximum:
- **+1 attacker dice roll**

---

### 5. Idea groups and policies

Patch 3.24 uses the standard broad idea-group structure, but the groups themselves are heavily rebalanced.

There are **25 selectable basic/government idea groups** in the Live ideas file:
- 7 Administrative
- 7 Diplomatic
- 11 Military/government-military

Notable examples:

##### Innovative
- Up to -35% advisor cost from completion.
- -10% technology cost.
- -10% institution embracement cost.
- +25% institution spread.
- Extra policies and free policy slots.
- Reduced war exhaustion and additional splendor.

##### Humanist
- -5 national unrest.
- -10 years of separatism.
- +2 accepted cultures.
- -50% culture-promotion cost.
- +10% manpower and -2.5% all-power cost.
- +50% improve relations.
- Stronger parliament support.

##### Spy
- State claims and vassal-transfer CB access.
- -20% AE.
- Doubled insult power projection.
- Large spy offence/defence bonuses.
- Stronger assault, looting, privateering and rebel-support effects.
- Better diplomatic-vassalisation acceptance.

##### Naval
- -80% naval barrage cost.
- Stronger naval leaders.
- +20% ship durability.
- +25% marine allowance.
- +1 blockade siege progress.
- Reduced landing penalty.

##### Infrastructure
- -15% development cost as completion bonus.
- Prosperity and state-maintenance bonuses.
- Cheaper construction and infrastructure expansion.
- Faster movement.
- Cheaper Centers of Trade.
- Lower state governing cost.
- Lower fort maintenance.
- Faster construction and monument upgrades.

##### Court
A much stronger ruler/estate/state-management group:
- Monarch DIP skill.
- Ruler lifespan.
- Estate equilibrium and privilege capacity.
- Free concentrate development.
- Reform progress.
- No stability loss on monarch death.
- Splendor.
- Absolutism and absolutism effect.

The policy matrix is also extensively rewritten: the three policy files currently contain **203 policy definitions**.

---

### 6. Institutions

Institutions are extensively reworked: their bonuses, historical spread and regional catch-up rules differ from vanilla.

##### Institution bonuses
- **Feudalism:** +1 free leader pool.
- **Renaissance:** -5% development cost and -5% construction cost.
- **Printing Press:** -5% stability cost.
- **New World / Colonialism:** +10% provincial trade power.
- **Global Trade:** +1 merchant.
- **Manufactories:** +10% global goods-produced size.
- **Enlightenment:** -25% culture-conversion cost.
- **Industrialization:** +25% manpower and +15% tax.

##### Timing and spread
- Renaissance is historically dated to **1330** in Florence and is already established across much of Italy by 1444.
- Printing Press is historically dated to **1452** in Frankfurt.
- Colonialism/New World remains a 1500-era institution.
- Spread rules contain extensive regional catch-up dates for Europe, the Americas, Africa, the Middle East and Asia.
- Institutions progressively increase **trade-company efficiency**, from +20% at Feudalism up to +500% at Industrialization.

---

### 7. Colonization and overseas empires

##### Colonial regions
The mod defines **20 colonial regions**, including Canada, Louisiana, California, Mexico, Caribbean, Colombia, Peru, La Plata, Brazil, Australia, Columbia, Hudson Bay, New Amsterdam, Georgia, Honduras, Texas, Congo, New Zealand, South Africa and the Philippines.

##### Trade companies
There are **61 trade-company regions**, including trade-company zones across Europe as well as Africa and Asia.

This includes nodes such as English Channel, North Sea, Lübeck, Baltic Sea, Sevilla, Genoa, Venice, Constantinople, Novgorod, Kiev, Persia, India, China, Japan and Polynesia.

##### Trade-company investments
Trade-company investments are rebalanced into stronger military, production, development, manpower and naval paths.

Examples:
- Defensive quarters increase local defensiveness and supply.
- Warehouses/depot investments increase production efficiency and trade power.
- Broker investments increase goods produced.
- Settlements/districts reduce development cost.
- Military administration can double local manpower and sailors.
- Unique 1000-ducat investments can grant force limit, trade steering, manpower/sailors or other country-wide benefits.

##### Colonial range
Patch 3.24 redistributes early colonial range:
- DIP 3 no longer gives the old +100 colonial range.
- DIP 5 gives +30 range.
- DIP 6 gives +70 range.
- DIP 7 continues the expansion with +115 range.

---

### 8. Subjects and liberty desire

Subject scaling is rebalanced.

- Standard vassal-style subjects use **0.15 liberty desire per development** instead of 0.25.
- Client vassals and client marches use only **0.05**.
- Colonial subjects use **0.15** instead of 0.10.
- Colonial subjects are prevented from gaining normal government rank.

Eyalets are also altered:
- Eyalet development contributes only 0.1 liberty desire per development.
- Eyalets transfer trade, pay a larger share of income and contribute force limit/manpower/sailors.
- Core Eyalets provide still larger contributions and can be integrated at a custom development cost.

---

### 9. Buildings and force-limit scaling

Patch 3.24 replaces the old province-triggered force-limit system with scripted scaling tied directly to province development.

##### Regimental Camp
- Base: +0.125 land force limit.
- Additional force limit scales upward every 5 development.
- Scaling reaches +3.75 at 75+ development.

##### Conscription Center
- Base: +0.25 land force limit.
- Additional scaling is roughly double the Regimental Camp.
- Scaling reaches +7.5 at 75+ development.

The correct modifier is recalculated when the building is constructed/upgraded, province development changes, development is exploited, development is concentrated, and during periodic five-year maintenance checks.

---

### 10. Espionage and diplomatic actions

##### Support Heir
Monarchies can use a large spy network and money to support a foreign heir/dynasty candidate, with costs scaling by target rank and a long cooldown.

##### Force Tributary CB
The Emperor of China can spend spy network to obtain a temporary Force Tributary CB:
- normally 80 spy network,
- reduced to 40 with completed Spy Ideas.

---

### 11. Netherlands and the VOC

The Netherlands can create a true VOC subject.

When established:
- Dutch-owned provinces in India and the East Indies are transferred to the VOC.
- VOC becomes a **Commercial Enterprise** subject.
- It receives a trade-company government, the overlord's religion/culture/technology group, large starting monarch power and treasury, and permanent claims across India and the East Indies.
- A later decision transfers further Dutch conquests to the VOC.

---

### 12. Multiplayer stalemate system

The mod contains a custom multiplayer anti-stalemate system.

- Up to three wars can be tracked simultaneously.
- Once activated, a war receives a **36-month stalemate timer**.
- A qualifying siege victory resets the timer to 36 months.
- The system cleans itself up when the tracked war ends.
- Dedicated decisions/events are used to declare and enforce stalemate handling.

---

### 13. Estates

Patch 3.24 adds a set of early-game estate advisor privileges.

New advisor privileges exist for:
- Nobility
- Burghers
- Clergy
- Rajputs
- Marathas
- Brahmins
- Vaishyas
- Ghulams

They generally trade:
- **-15% advisor cost** in the estate's monarch-power category,
- estate loyalty/influence,
- higher stability cost,
- and reduced maximum absolutism.

Most are restricted to smaller countries, giving sub-100-development states an additional early-game estate choice.

---

### 14. Advisors and ruler personalities

- **79 additional advisor types** are defined.
- **69 additional ruler personalities** are defined.

These include court, religious, administrative, trade, colonial, naval, military and estate-specific roles and traits.

---

### 15. Government reforms and mechanics

The government system is substantially expanded beyond vanilla.

The branch includes custom mechanics for, among others:
- Imperial Grace
- Imperial Control
- Bureaucracy vs. Military
- Catholics vs. Utraquists
- Crusade Power
- Italian Condottieri
- Italian Dynasties
- Legitimation through Representation
- Liberalism
- Maritime Republics
- Political Focus
- Republican vs. Monarchist politics
- Rajput principalities
- Sicilian government
- Venetian government
- Pratihara mechanics

There are also many additional country- and region-specific reforms for Europe, India, Persia, Africa and East Asia.

---

### 16. Monuments

Three major custom monuments are added.

##### Fortress of Luxembourg
A very powerful defensive monument with fort levels, garrison size, defender dice, defensiveness and hostile attrition.

##### Rock of Gibraltar
Focused on controlling Gibraltar with fort levels, supply, hostile fleet attrition, very high defensiveness and defender dice at tier 3.

##### Hohenzollern Castle
Provides prestige and defensive bonuses, with much stronger conditional military bonuses when owned by the **von Hohenzollern** dynasty.

---

### 17. Events, disasters and historical content

The mod bundles a large amount of extra historical/flavour content beyond vanilla.

Notable additional or expanded disaster/event systems include:
- Decline of the Ottomans
- Mughal crisis/decline
- Bahmani decline
- Vijayanagar decline
- Partition of Poland
- Portuguese Succession Crisis
- Rampjaar
- Pashtun Uprising
- Zemene Mesafint
- Habsburg-monarchy division
- Italian republican crises
- Vijayabahu Kollaya
- Fall of Cromwell
- Chmielnicki uprising

There are also large country-event packages for Austria, France, Britain, Iberia, Russia, Persia, India, the Netherlands, Italy, the HRE and many smaller countries.

---

### 18. Country, map and historical setup changes

The Live branch contains extensive country-history, province-history, diplomacy and tag changes.

These include:
- altered 1444 country/province setups,
- additional formable or alternate states,
- custom country names and colours,
- expanded colonial naming,
- modified HRE history/diplomacy,
- changes across Oceania and other underrepresented regions,
- and many country-specific historical adjustments.

The mod therefore should not be treated as vanilla's 1444 map with only balance changes; its historical setup is materially different.

---

### 19. Patch 3.24 release highlights

While the rest of this document is cumulative versus vanilla, the **3.24 release itself** is especially centered on:

- Development-scaled force-limit buildings.
- Replacement of the old triggered-modifier force-limit system.
- New estate advisor privileges.
- Reworked Humanist government-reform requirements/effects.
- Redistribution of early colonial range across DIP technology.
- Further colonial-region and trade-company restructuring.
- New/reworked trade-company investments.
- Stalemate-system revisions.
- Several map/history cleanup changes.

## v3.24.2 <small>[? Back to top](#changelogs)</small>

**Compared against:** 3.24  
**3.24 commit:** `5e2908ce277c047279b62a3144e979dc86d18de0`  
**3.24.2 commit:** `d10ad43416aa49935f9d5199310bf914bed74df2`

This changelog compares the actual repository snapshots of 3.24 and 3.24.2, so intermediate unnumbered commits and 3.24.1 are only represented where their changes still exist in 3.24.2.

#### Age modifier system

- Reworked the large age-based triggered-modifier system.
- **88 triggered modifiers** were moved out of `common/triggered_modifiers/` and rebuilt as normal country modifiers controlled through scripted effects and on-actions.
- The new system refreshes applicable age bonuses during a yearly pulse and refreshes/cleans them when a new age begins.
- This covers:
  - **30** Age of Discovery modifiers
  - **21** Age of Reformation modifiers
  - **15** Age of Absolutism modifiers
  - **5** industrial/imperial-era modifiers handled by the age script
  - **17** Age of Revolutions modifiers
- The underlying bonuses are largely carried over; the main change is how and when the game evaluates/applies them.
- Because they are no longer continuously evaluated as triggered modifiers, gaining or losing the conditions for one of these bonuses is now handled through the refresh pulse rather than instant continuous recalculation.

Examples of migrated bonuses include development milestones, country-specific age bonuses, merchant-region bonuses, religious-unity and culture-unification bonuses, and colonial/trade/military/industrial/government-condition bonuses.

#### Colonial range

The early colonial-range redistribution introduced in 3.24 has been reverted.

- **Diplomatic Technology 3** once again grants **+100 colonial range**.
- The extra **+30 colonial range at Diplomatic Technology 5** has been removed.
- The extra **+70 colonial range at Diplomatic Technology 6** has been removed.
- Diplomatic Technology 7 retains its existing **+115 colonial range**.

#### Country renaming

##### Bavaria
Bavaria can now use the country-renaming decision.

New options:
- **Kingdom of Bavaria**
- **Königreich Bayern**
- **Volksstaat Bayern**

##### France
New rename options:
- **European Union**
- **Frankish Empire**

##### Mamluks
New rename options:
- **Kemet**
- **Deshret**
- **Heka**
- **Gift of the Nile**
- **Mother of the World**

##### Italy
New rename option:
- **Italia**

Also fixed the adjective for **La Repubblica Italiana** from `Itanian` to `Italian`.

##### Portugal
New rename options:
- **Reino de Portugal**
- **Regnum Portugalliae**
- **Regnum Portugalliae et Algarbiae**

##### Morocco
New rename options:
- **Maghreb al-Aqsa**
- **Marinids**
- **Dar al-Maghrib**

##### Ottomans
The Ottoman/Turkey rename list has been heavily cleaned up.

The custom options now include:
- **OwOman Empire**
- **Ottoman Sultanate**
- **Ottoman Empire**
- **Rûm**

Removed rename choices include:
- The Eastern Sun
- Osman
- Osman Sultanate
- Osman Empire
- Turkish Empire
- Turkish Republic
- Republic of Turkey
- Turkey
- Sublime Osman State
- Devlet-i Aliyye-i Osmaniyye
- Devlet-i Rûm

#### Technical summary

Between the exact 3.24 and 3.24.2 snapshots, **95 files differ**:

- **88** old triggered-modifier files removed as part of the age-modifier migration.
- `common/event_modifiers/aet_modifiers.txt` expanded with the replacement modifiers.
- `common/scripted_effects/aet_scripted_effects.txt` expanded with the new age refresh/apply logic.
- `common/on_actions/aet_on_actions.txt` updated to refresh those modifiers yearly and on age changes.
- `common/technologies/dip.txt` changed for the colonial-range rollback.
- `decisions/country_rename.txt`, `events/lukat_events.txt`, and `localisation/rename_country_l_english.yml` changed for the rename additions and cleanup.

## v3.24.3 <small>[? Back to top](#changelogs)</small>

**Compared against:** 3.24.2  
**3.24.2 commit:** `d10ad43416aa49935f9d5199310bf914bed74df2`  
**3.24.3 commit:** `942912ebe5b050787b96f76ad9af962580cacbcd`

This changelog compares the exact numbered release snapshots of **3.24.2** and **3.24.3**.

#### Core Eyalets

- Fixed **Core Eyalets** so they can now be diplomatically annexed.
- This restores the annexation option that was previously missing for Ottoman Core Eyalet subjects.
- Cleaned up the Eyalet subject-type definitions to avoid redundant empty declarations.

#### Elections
*Note to Reader: This entire Section is wrong but the original version was preserved*
- Fixed ruler generation in one of the republican election paths.
- Election-generated rulers now receive explicit base stats instead of relying on stat-change commands without a defined starting value.
- The **Republican Virtues** path now starts the new ruler at **2/2/2** before applying its +1 ADM / +1 DIP / +1 MIL bonuses.
- The alternate female-ruler branch now creates a **2/2/2** ruler directly.

#### Colonial regions

- Corrected the **Caribbean colonial region** by removing province **481** (Bermuda) from its province list.

## v3.25.1 <small>[? Back to top](#changelogs)</small>

**Compared against:** 3.24.3  
**3.24.3 commit:** `942912ebe5b050787b96f76ad9af962580cacbcd`  
**3.25.1 commit:** `6943039d6549c7bc306e85342f6ca3ebe437e1c6`

This changelog compares the exact numbered release snapshots of **3.24.3** and **3.25.1**.

#### Holy Roman Empire overhaul removed

The integrated **Besseres HRR / Holy Roman Empire Expanded** content has been removed from the mod.

#### Dutch elections

The Dutch election event has been substantially rewritten.

- Statist candidates are now generated before the election choice is resolved.
- Candidate generation now uses randomized ADM/DIP/MIL stats rather than the fixed 2/2/2 setup used previously.
- Generated candidates receive +1 to each monarch skill after generation.
- Countries with **Republican Virtues** receive another +1 ADM, +1 DIP and +1 MIL.
- Each monarch skill also has an independent **6.25% chance** to gain another +1.
- The generated Statist candidate is stored and then installed when the Statist option is selected.
- Temporary ruler/heir handling was added so candidate generation can occur without permanently disrupting the country used as temporary scope.

> Note: This explanation is not entirely correct. However it comes close to correct. Monarch pips get randomised as common, clamped to [0, 4] and improved by 1. After that the other steps explained here get applied. This uses a system similar to lottery elections (the last two points).

#### Country renaming

A set of new country-renaming options has been added.

##### Netherlands
- Verenigde Provinciën der Caraïben
- Vrije Handelsstaat
- Groot Koopmansrijk
- Verenigde Koopmansstaten

##### Kongo
Kongo can now use the rename-country decision.

New options:
- Kingdom of Kongo
- Christian Realm of Kongo
- Democratic People's Republic of Kongo
- United Kongolese States

##### Switzerland
- Pagonia Unita Alpinorum
- Republik delle Alpes
- Civitatum Raetiae et Noricum


## v4.0.0 <small>[? Back to top](#changelogs)</small>

*Not yet released.*

### Ideas Rework

### Elite Guards

### Development

- Introduced a project documentation system (see the [docs readme](README.md)). To date documentation of legacy content is not yet complete.
- Established formal versioning.
- Changed the versioning model from **commit = version** to **publication = version**.
- Moved changelogs into this file 