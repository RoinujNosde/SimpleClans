# Changelog

## [2.19.1](https://github.com/RoinujNosde/SimpleClans/compare/v2.19.0...v2.19.1) (2023-08-03)


### Bug Fixes

* charge money on deposit ([#394](https://github.com/RoinujNosde/SimpleClans/issues/394)) ([fe60b25](https://github.com/RoinujNosde/SimpleClans/commit/fe60b25f6107281a9771f6498400c3f8ce81c916))
* consider uppercase when checking for disallowed colors ([#391](https://github.com/RoinujNosde/SimpleClans/issues/391)) ([503d679](https://github.com/RoinujNosde/SimpleClans/commit/503d67903e386b1c4b6eb7903a1addbfd42a684a))

## [2.19.0](https://github.com/RoinujNosde/SimpleClans/compare/v2.18.1...v2.19.0) (2023-07-27)


### Features

* add %simpleclans_has_rank% placeholder ([#379](https://github.com/RoinujNosde/SimpleClans/issues/379)) ([f8ce0f4](https://github.com/RoinujNosde/SimpleClans/commit/f8ce0f415f17748f8b4382e028dd1f5f93cae06a))
* add a command to set player's locale ([#378](https://github.com/RoinujNosde/SimpleClans/issues/378)) ([5cf64a6](https://github.com/RoinujNosde/SimpleClans/commit/5cf64a672d0a25a62d3abd6720082bb3b4cf056f))
* add confirmation before reset kdr ([#371](https://github.com/RoinujNosde/SimpleClans/issues/371)) ([d4f1a74](https://github.com/RoinujNosde/SimpleClans/commit/d4f1a74dee9cdeeb458ff6975715a817bfe30fa1))
* add PlayerResetKdrEvent ([#370](https://github.com/RoinujNosde/SimpleClans/issues/370)) ([d68ec28](https://github.com/RoinujNosde/SimpleClans/commit/d68ec28f8e14926cbe9d13c09a61735e5304a97d))
* add subcommand /clan mod bb display ([#367](https://github.com/RoinujNosde/SimpleClans/issues/367)) ([105a8ca](https://github.com/RoinujNosde/SimpleClans/commit/105a8cad51b12bf6f487cf6c2b81eb85d966a3cc))
* adds the possibility to rename a clan ([#360](https://github.com/RoinujNosde/SimpleClans/issues/360)) ([0f69332](https://github.com/RoinujNosde/SimpleClans/commit/0f693329cae81036bfe1423344ed41cf3d535f64))
* bb message language key ([#358](https://github.com/RoinujNosde/SimpleClans/issues/358)) ([263d129](https://github.com/RoinujNosde/SimpleClans/commit/263d129325888fb1303dda549986ab43ca93ed13))
* **chat:** adds permissions to use colors and formats ([#341](https://github.com/RoinujNosde/SimpleClans/issues/341)) ([2a139ab](https://github.com/RoinujNosde/SimpleClans/commit/2a139aba03be174086dfb3603007312d0c7a6c33))
* EconomyTransactionEvent creation ([#384](https://github.com/RoinujNosde/SimpleClans/issues/384)) ([d21dc02](https://github.com/RoinujNosde/SimpleClans/commit/d21dc02dff45f4f20a40303cd26215a71de7dddb))
* hide reset kdr in main frame if perm denied ([#385](https://github.com/RoinujNosde/SimpleClans/issues/385)) ([ad6bb7b](https://github.com/RoinujNosde/SimpleClans/commit/ad6bb7b13b347be71789fbfb4b8a82f6c5481c96))
* validating tag by regex ([#363](https://github.com/RoinujNosde/SimpleClans/issues/363)) ([3ef3169](https://github.com/RoinujNosde/SimpleClans/commit/3ef3169942f897280af15040f1243d955df001ab))


### Bug Fixes

* added clan suggestion in staff bb commands ([#359](https://github.com/RoinujNosde/SimpleClans/issues/359)) ([442096e](https://github.com/RoinujNosde/SimpleClans/commit/442096e9da98410682c2cd09e0e930454fe0364f))
* async permission reset & category deletion ([#347](https://github.com/RoinujNosde/SimpleClans/issues/347)) ([7eb748b](https://github.com/RoinujNosde/SimpleClans/commit/7eb748bb9a31a826f53fd7ac6b1eeac64e330467))
* bump JDK to 16 ([e368ead](https://github.com/RoinujNosde/SimpleClans/commit/e368ead04aa2735f5525d88d5dcb63b78286f80e))
* **bungee:** removes players from clan on delete ([0258a75](https://github.com/RoinujNosde/SimpleClans/commit/0258a75c819f12e5dbbaf383a572682717f34c59))
* **bungee:** updates kills count immediately ([ed243f7](https://github.com/RoinujNosde/SimpleClans/commit/ed243f72ecc58a3af11d053d73b315bce9e20e94))
* default username-regex ([bf1033a](https://github.com/RoinujNosde/SimpleClans/commit/bf1033af769bacbca9a790d2184ec77eeb519485))
* **discord:** permission overrides reset only existing members ([#349](https://github.com/RoinujNosde/SimpleClans/issues/349)) ([8a35293](https://github.com/RoinujNosde/SimpleClans/commit/8a3529377f5b4a482b530e474e4921b311197a04))
* display hex message on bulletin board ([#365](https://github.com/RoinujNosde/SimpleClans/issues/365)) ([0b74355](https://github.com/RoinujNosde/SimpleClans/commit/0b74355467625a4901944c72feb84d8de91a4283))
* invalid colors being used in tags ([2850e59](https://github.com/RoinujNosde/SimpleClans/commit/2850e59d370da34c643de198de85d91744610467))
* invalid colors being used in tags, pt. 2 ([f98868c](https://github.com/RoinujNosde/SimpleClans/commit/f98868c776a2e275dc7c1647748a0231e935f43e))
* migrates land permissions on GP claim resize ([2f4ad00](https://github.com/RoinujNosde/SimpleClans/commit/2f4ad00f1c72ee7e3948267965c3a075ec4ef728))
* NPE on EntityDamageByEntityEvent listener (LandProtection) ([524e407](https://github.com/RoinujNosde/SimpleClans/commit/524e40762011dd500a407286b72b51acf6c357f3))
* players not able to break vehicles/exit horses ([86d4fac](https://github.com/RoinujNosde/SimpleClans/commit/86d4fac938e353f94c288c39b884027027972bba))
* players not able to sleep on beds (WorldGuard) ([e9ec283](https://github.com/RoinujNosde/SimpleClans/commit/e9ec283ace55af10abdeb6f937d31d4c2dc198dc))
* players not able to strip logs/fertilize blocks ([5021b8a](https://github.com/RoinujNosde/SimpleClans/commit/5021b8ad7b5633769bd87af96a0226a53a8b24b4))
* players not able to use water buckets (WorldGuard) ([807d39a](https://github.com/RoinujNosde/SimpleClans/commit/807d39afeb51d99a96fb793099393b44e2c43b04))
* players unable to interact with armor stands, paintings, etc ([61dcb3d](https://github.com/RoinujNosde/SimpleClans/commit/61dcb3d440e2eedfb717afcc69687194311a8973))
* possibly fixes CME on SpyChatHandler ([8a05dbb](https://github.com/RoinujNosde/SimpleClans/commit/8a05dbbf00df97afa7df71a500e2626aaa46ed37))
* prevent clan name renaming with colors ([#374](https://github.com/RoinujNosde/SimpleClans/issues/374)) ([7fcb964](https://github.com/RoinujNosde/SimpleClans/commit/7fcb964f41118e7d8746e87d5770cc329730834c))
* remove abandoned channels before validating ([#348](https://github.com/RoinujNosde/SimpleClans/issues/348)) ([5f519c4](https://github.com/RoinujNosde/SimpleClans/commit/5f519c477af3e759596017010d51ddd06e26157e))
* resets error message when validating a clan tag ([#383](https://github.com/RoinujNosde/SimpleClans/issues/383)) ([8269d80](https://github.com/RoinujNosde/SimpleClans/commit/8269d80831533e517ed2ebd94bec18021de7b0e3))
* sets duplicate names to something unique ([046aa1d](https://github.com/RoinujNosde/SimpleClans/commit/046aa1d96d58e21496a3361c7d072f3943595304))
* tnt explosion doesn't count a kill ([#381](https://github.com/RoinujNosde/SimpleClans/issues/381)) ([57b9987](https://github.com/RoinujNosde/SimpleClans/commit/57b9987e9b9dade59c52c88dbfd3b12fdf5eee94))
* view permission applies only to cached channels ([#346](https://github.com/RoinujNosde/SimpleClans/issues/346)) ([1850701](https://github.com/RoinujNosde/SimpleClans/commit/18507016a843c28efc6d8109f2bb5a5106531f47))


### Performance Improvements

* **discord:** remove discord channels from creation ([#353](https://github.com/RoinujNosde/SimpleClans/issues/353)) ([36084db](https://github.com/RoinujNosde/SimpleClans/commit/36084dbfa3e73aedccb156e49af5062e40639927))


### Documentation

* author tag isn't available at methods ([f5d547a](https://github.com/RoinujNosde/SimpleClans/commit/f5d547ab27fac7cfe1abd9c819e4f1bb3dd7c624))
* updated version in README ([#343](https://github.com/RoinujNosde/SimpleClans/issues/343)) ([718868b](https://github.com/RoinujNosde/SimpleClans/commit/718868b2d9eea2aeb6fcfd734e85d16a2cefaa36))

## [2.18.1](https://github.com/RoinujNosde/SimpleClans/compare/v2.18.0...v2.18.1) (2022-11-29)


### Bug Fixes

* added check for clan_member on land commands ([fc81e76](https://github.com/RoinujNosde/SimpleClans/commit/fc81e765211bebe22a7a6aa5d5158754580bbdc5))
* attempt at fixing NPE on getAllClanPlayers ([26ba9f7](https://github.com/RoinujNosde/SimpleClans/commit/26ba9f73296a772eaa698d2ede6fbc3831da850c))
* **bungee:** error deserializing clan banners ([56c7908](https://github.com/RoinujNosde/SimpleClans/commit/56c7908c332167cec9cca6a4ea054977d0d68b9f))
* channel auto creation setting not respected on join/link ([7f9d133](https://github.com/RoinujNosde/SimpleClans/commit/7f9d133051b9011837d0a5114abc6c3637a3f02c))
* default ranks were getting re-added on config load ([7a37382](https://github.com/RoinujNosde/SimpleClans/commit/7a37382264ff4dd5456f6930045b5102bf491897))
* missing getHandlerList on HomeRegroupEvent ([#338](https://github.com/RoinujNosde/SimpleClans/issues/338)) ([8fa465d](https://github.com/RoinujNosde/SimpleClans/commit/8fa465da385f36b1fb075031c375ef94df78ea52))
* money was not given back when channel creation failed ([#336](https://github.com/RoinujNosde/SimpleClans/issues/336)) ([d8cb61b](https://github.com/RoinujNosde/SimpleClans/commit/d8cb61b0fc9584bb58f421faf98596816621053f))
* wrong path of discord economy section ([#334](https://github.com/RoinujNosde/SimpleClans/issues/334)) ([3f1b06d](https://github.com/RoinujNosde/SimpleClans/commit/3f1b06dcf996e51a878b7fc281069e236ebcbfce))

## [2.18.0](https://github.com/RoinujNosde/SimpleClans/compare/v2.17.0...v2.18.0) (2022-10-14)


### Features

* add help-size config ([25197cd](https://github.com/RoinujNosde/SimpleClans/commit/25197cdd729cea63d7e223dc1e0aaef830613327))
* added method for registering protection providers ([2ea7444](https://github.com/RoinujNosde/SimpleClans/commit/2ea744443f502970b15859818e9d5e45ca69d453))
* adds permission to bypass the teleport delay ([d56a0a9](https://github.com/RoinujNosde/SimpleClans/commit/d56a0a979a47a140115742518e934a2469e6f880))
* adds PlayerHomeClearEvent ([becb88b](https://github.com/RoinujNosde/SimpleClans/commit/becb88b763380bed5a2e1dd209c32c8363a7f88b))
* adds starter-ranks ([a440834](https://github.com/RoinujNosde/SimpleClans/commit/a4408343479d7efa6ae5a65894f8b822ecd47ebb))
* Improves Bungee support ([#327](https://github.com/RoinujNosde/SimpleClans/issues/327)) ([2c9b0f9](https://github.com/RoinujNosde/SimpleClans/commit/2c9b0f98fe3942ea74190c1a81d61be2c8647db8))
* **lang:** new crowdin updates ([#319](https://github.com/RoinujNosde/SimpleClans/issues/319)) ([87ece22](https://github.com/RoinujNosde/SimpleClans/commit/87ece22036fb674c8c362b6a1b86fe884543e6ad))


### Bug Fixes

* changes size of some frames for better visualization on Bedrock ([1adec0b](https://github.com/RoinujNosde/SimpleClans/commit/1adec0b89d676767cdb94dcdf91ba37a231eb4a8))
* ConfigurationSerializableAdapter NPE ([30a501c](https://github.com/RoinujNosde/SimpleClans/commit/30a501c063aa9dbcf596df83adc2b15baf0e4bb9))
* getPastClans() preserves the insertion order ([af07988](https://github.com/RoinujNosde/SimpleClans/commit/af079881030bd299b9bae1ebf0cc62353af7e6fc))
* NoClassDefFoundError in WorldGuard6Provider ([a859928](https://github.com/RoinujNosde/SimpleClans/commit/a8599286225ba603ea124b8b52510245b94ffd24))
* NPE while collecting fees ([c47289c](https://github.com/RoinujNosde/SimpleClans/commit/c47289c5bf8113a9d6a8e1ee74a5afb0358647b6))
* NPE while purging data ([f14c425](https://github.com/RoinujNosde/SimpleClans/commit/f14c42548846feef01b484acbb5f02bcbcabd8db))
* NPE while sending a message to spies ([#322](https://github.com/RoinujNosde/SimpleClans/issues/322)) ([e7f756f](https://github.com/RoinujNosde/SimpleClans/commit/e7f756f3063c646e21415e08eea73f123d2818a0))
* removes duplicate /clan from help ([f7e7ec6](https://github.com/RoinujNosde/SimpleClans/commit/f7e7ec66243359e26d3c456a9740a53753951c48))
* removes rank and join date on disband ([2a1ad5b](https://github.com/RoinujNosde/SimpleClans/commit/2a1ad5b5c8d2937a8ee6dd6f7f43d85c86f08307))
* set useSSL to false ([f484bc1](https://github.com/RoinujNosde/SimpleClans/commit/f484bc16c6176ccd13e05aae2169400e7bcf5d62))
* supports '.' and '*' as username prefix by default ([37f0ecf](https://github.com/RoinujNosde/SimpleClans/commit/37f0ecff506d06f3047e3598848117a14c2b7ead))
* updates join date when joining a clan ([#326](https://github.com/RoinujNosde/SimpleClans/issues/326)) ([96a13d8](https://github.com/RoinujNosde/SimpleClans/commit/96a13d8d6cc18c91d4dee20dd632cd5d65db5213))


### Performance Improvements

* improved UUID lookup performance ([4ab53d1](https://github.com/RoinujNosde/SimpleClans/commit/4ab53d16135ecf5a4067b1a63d058da392dc5313))
