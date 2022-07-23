# Zinemite

Simple PDF hosting with files and database stored in S3.
Personal learning project for getting a hang of [kit](https://github.com/kit-clj/kit), [Litesream](https://litestream.io) and S3.


## Deploy

ENV vars to set:

```
S3_ACCESS_KEY=<access_key>
S3_SECRET_KEY=<secret_key>
S3_BUCKET=<bucket>
S3_ENDPOINT=https://s3.amazonaws.com  #S3 provider to use
S3_REGION=<region>  #Required by Litestream
S3_DB_PATH=zinemite_prod.db  #Path to store DB in S3
DB_PATH=/zinemite/zinemite_prod.db  #Local DB path
DB_SYNC_INTERVAL=15s
JDBC_URL=jdbc:sqlite:/zinemite/zinemite_prod.db  #Must contain the same as DB_PATH
AWS_EC2_METADATA_DISABLED=True  #AWS SDK will be slower without
BASE_URL=https://example.com  #External URL for site
FOOTER_CONTENT=<p class='text-muted'>Your contact here</p>
```

Optional ENV vars:
```shell
MAX_FILE_SIZE=<int in megabytes> # By default 20
```

These can be set in a `.env` file for example, for use with Docker:

```shell
docker build -t yourname/zinemite
docker run --env-file .env -p 3000:3000 yourname/zinemite
```


## Dev

Start a [REPL](#repls) in your editor or terminal of choice.

Start the server with:

```clojure
(go)
```

The default API is available under http://localhost:3000/api

System configuration is available under `resources/system.edn`.

To reload changes:

```clojure
(reset)
```

## REPLs

### Cursive

Configure a [REPL following the Cursive documentation](https://cursive-ide.com/userguide/repl.html). Using the default "Run with IntelliJ project classpath" option will let you select an alias from the ["Clojure deps" aliases selection](https://cursive-ide.com/userguide/deps.html#refreshing-deps-dependencies).

### CIDER

Use the `cider` alias for CIDER nREPL support (run `clj -M:dev:cider`). See the [CIDER docs](https://docs.cider.mx/cider/basics/up_and_running.html) for more help.

Note that this alias runs nREPL during development. To run nREPL in production (typically when the system starts), use the kit-nrepl library through the +nrepl profile as described in [the documentation](https://kit-clj.github.io/docs/profiles.html#profiles).

### Command Line

Run `clj -M:dev:nrepl` or `make repl`.

Note that, just like with [CIDER](#cider), this alias runs nREPL during development. To run nREPL in production (typically when the system starts), use the kit-nrepl library through the +nrepl profile as described in [the documentation](https://kit-clj.github.io/docs/profiles.html#profiles).
