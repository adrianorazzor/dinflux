# Potential Bugs Identified

## 1. Invalid Exposed package imports
Several Kotlin files import classes from the `org.jetbrains.exposed.v1` namespace, for example `DatabaseFactory`, `User`, and `UserRepository`. The Exposed ORM artifacts published in `build.gradle.kts` (`exposed-core`, `exposed-dao`, `exposed-jdbc`, etc.) expose their APIs under `org.jetbrains.exposed.sql` (and related) packages. Attempting to compile with the `.v1` packages results in unresolved references and a build failure. The imports should be switched back to the canonical Exposed packages.

## 2. Registration stores untrimmed e-mail addresses
`AuthService.register` writes whatever string the HTTP layer passes in to the database without trimming it, while `AuthRoutes.login` trims the submitted e-mail before looking it up. This asymmetry means that if someone signs up with leading/trailing whitespace in their address, the stored value keeps the whitespace but login will query with the trimmed address and fail to find the user.

## 3. `display_name` migration leaves old rows null
Migration `V2__add_display_name_column.sql` adds the `display_name` column without a default value or backfill, so existing `users` rows will have `NULL` in that column. The Exposed model marks `Users.displayName` as a non-null `varchar`, so reading an older row will fail at runtime. The migration should either backfill a value or declare the column `NOT NULL DEFAULT ''` (and optionally update), or the model should allow nulls until the data is cleaned up.
