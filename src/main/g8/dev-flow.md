# Developer flow

## Local docs

Docs are based on:

- `docsify`, a _dynamic_ , markdown based generator.
- `mdoc`, typechecked scala/markdown compiler

The source for the docs is in `yourProject/docs`, the website in
`yourProject/target/website`. The currently deployed website is in the
`gh-pages` branch.

To preview the site locally, you need to install:

```
npm i docsify-cli -g
```

then, start mdoc in an sbt session:

```
sbt docs/mdoc --watch
```

and docsify in a shell session:

```
cd yourProject/target/website
docsify serve .
```

and you'll get an updating preview.
Note that `mdoc` watches the markdown files, so if you change the code
itself it will need a manual recompile.

`docsify` uses 3 special files: `index.html`, `_coverpage.md`, `_sidebar.md`,
the sidebar needs to have a specific format:

- newlines in between headers
- and no extra modifiers inside links `[good]`, `[**bad**]` (or collapse will not work)

## Release

Push a `vx.y.z` tag on `main` to release. It will fail if semver isn't
respected wrt bincompat.
Docs are released automatically on each code release, if you need a
docs-only deploy, (force) push `main` to the `docs-deploy` branch.

To change/add branches to release:

> ThisBuild / spiewakMainBranches := Seq("main", "develop")

To relax semver:

> ThisBuild / strictSemVer := false

To publish snapshot on every main build:

> ThisBuild / spiewakCiReleaseSnapshots := true

Caveat:
If you are publishing snapshots, you need to make sure that new
commits are fully built before you push a proper release tag: push
`main`, wait for the snapshot release to complete, and then push the
tag.

## Links

- https://github.com/djspiewak/sbt-spiewak
- https://github.com/djspiewak/sbt-github-actions
- https://docsify.js.org/#/
- https://scalameta.org/mdoc/


## Initial setup

These steps only need to be done once when setting up the repo.

### Main branch

Make sure the default branch is named `main`

### Doc setup

On your main branch, go in `docs/index.html` and, if necessary, change:

- title
- description
- name
- repo

to match your project.

Then, create and push a `gh-pages` branch:
 - `git checkout --orphan gh-pages`
 - Remove all files from the new branch
 - Add an empty commit
 - Push

Finally, Enable Github Pages on that branch in Github settings.

### CI setup

Generate ci definitions with `sbt githubWorkflowGenerate` and commit the results.

Then, configure the following encrypted secrets within GitHub Actions:

- SONATYPE_USERNAME
- SONATYPE_PASSWORD
- PGP_SECRET

Once you know the value for a secret, it can be created by going to

> Your repo -> settings -> left sidebar -> Secrets and variables -> Actions -> new repository secret.

populate sonatype user and password with a user token:

- login to https://oss.sonatype.org,
- click your username in the top right, then profiles,
- in the tab that was opened, click on the top left dropdown, and select "User Token",
- click "Access User Token", and you'll get the name and password parts of the token

populate pgp_secret with a new per project key. This assumes you have
`gpg` and a personal key already setup.

Generate a new key with `gpg --gen-key`

- Use `yourProject bot` as name
- Use your email address as email
- Leave the passphrase empty: we need to export the key with no
  passphrase since it will be encrypted by github actions. Only use
  this key for this project, don't use your personal key

You can `gpg --list-secret-keys` to view the id of the key you have generated.

Now export the key

> gpg --export-secret-keys yourKeyId | base64 | pbcopy

and paste it the PGP_SECRET Github Secret.

Then sign the new project key with your personal key:

> gpg --sign-key yourKeyId

and publish it to a keyserver

> gpg --keyserver http://keyserver.ubuntu.com:11371/ --send-key yourKeyId


Additional links:

- https://docs.github.com/en/free-pro-team@latest/actions/reference/encrypted-secrets
- https://github.com/jodersky/sbt-gpg
- https://github.com/olafurpg/sbt-ci-release

### Troubleshooting

If you see an error that mentions:

> Inappropriate ioctl for device

while trying to publish, it means you have created your key with
passphrase protection instead of exporting it unencrypted.
You can verify this is the case by running:

> gpg --export-secret-keys yourKeyId | base64 | pbcopy

If it asks you to insert your passphrase, you do have passphrase
protection. To fix:

> gpg --edit-key yourKeyId

then type:

> passwd

you will need to insert your passphrase, and then leave the new
passphrase blank, pressing `Enter` as required. Finally, type:

> save

Now, export the key with:

> gpg --export-secret-keys yourKeyId | base64 | pbcopy

and update your PGP_SECRET Github Secret.

### Finish

You should be all set and ready to go, feel free to delete the
`Initial Setup` section of this doc.


## GPG CI failure

After months or years of working, you might see CI failures with:

```
[info] gpg: no default secret key: No secret key
[info] gpg: signing failed: No secret key
[info] gpg: no default secret key: No secret key
[info] gpg: signing failed: No secret key
```

This means that your key is expired.

> gpg --list-keys

will show `[expired yyyy-mm-dd]` next to the expired key.

> gpg --edit-key yourKeyId

enters the interactive editing setup, use 

> expire

to renew the key (lol), it will prompt you for a duration.

Then,

> key 1

to select the subkey (you will see a start next to it), again

> expire

and follow the prompt.

Finally,

> save

and

> gpg --keyserver keyserver.ubuntu.com --send-keys yourKeyId

to publish.

Finally, you have to update the secret in github again:

Export the key

> gpg --export-secret-keys yourKeyId | base64 | pbcopy

and paste it in the PGP_SECRET Github Secret.

> Your repo -> settings -> left sidebar -> Secrets and variables -> Actions -> PGP_SECRET -> update icon.

Then restart the CI workflow.

Bitrot narrowly averted again.
