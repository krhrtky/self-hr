.PHONY: db-up, setup, db-migrate-local, db-migrate-remote,db-codegen, api-image, start-backend, start-frontend, build-backend, build-frontend, setup-frontend, test-frontend, start-aws-mock, setup-terraform

db-up:
	@if ! docker compose ps | grep -q db; then \
		docker compose up db -d; \
		echo "Waiting for db service to be ready..."; \
		until docker compose exec db pg_isready &> /dev/null; \
		do \
			sleep 1; \
		done; \
	fi

setup: setup-backend setup-frontend

setup-backend: db-up db-migrate-local db-codegen

setup-frontend: install-frontend open-api-client-gen

setup-terraform:
	cd infrastructure && sh ./bin/setup.sh

DB_MIGRATE_COMMAND = docker compose run --rm sqldef psqldef

dry-db-migrate-local:
	${DB_MIGRATE_COMMAND} -h db -U $(DB_USER) -W $(DB_PASSWORD) $(DB_NAME) --file=./volume/schema.sql --dry-run

dry-db-migrate-remote:
	${DB_MIGRATE_COMMAND} -h $(DB_HOST) -U $(DB_USER) -W $(DB_PASSWORD) $(DB_NAME) --file=./volume/schema.sql --dry-run

db-migrate-local:
	${DB_MIGRATE_COMMAND} -h db -U root -W password  app --file=./volume/schema.sql

db-migrate-remote:
	${DB_MIGRATE_COMMAND} -h $(DB_HOST) -U $(DB_USER) -W $(DB_PASSWORD) $(DB_NAME) --file=./volume/schema.sql

db-seed:
	docker compose exec -d db sh /data/bin/seed.sh

db-codegen:
	./gradlew backend:infrastructure:generateJooq

graphql-codegen:
	./gradlew backend:api:generateJava

open-api-schema-gen:
	./gradlew backend:api:generateOpenApiDocs

open-api-client-gen: open-api-schema-gen
	${FRONT_APP_COMMAND} api-code:gen

FRONT_APP_COMMAND = pnpm -F "app"

start-backend:
	./gradlew backend:api:bootRun --debug-jvm

run-storybook:
	${FRONT_APP_COMMAND} storybook


start-frontend:
	${FRONT_APP_COMMAND} dev

build-backend:
	./gradlew backend:api:bootJar

install-frontend:
	pnpm recursive install

build-frontend:
	${FRONT_APP_COMMAND} build

test-frontend:
	${FRONT_APP_COMMAND} test

start-aws-mock:
	@if ! docker compose ps | grep -q moto-aws-local; then \
		docker compose up moto-aws-local -d; \
		echo "Waiting for moto-aws-local service to be ready..."; \
		until curl --silent http://localhost:4000; \
		do \
			sleep 1; \
		done; \
		docker compose run aws-local-cli bash /aws-local-cli/.bin/create-user-pool.sh; \
	fi
