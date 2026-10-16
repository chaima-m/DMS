# DMS --- Document Management System

DMS is a document-management project organized as a set of services,
with a React-based web interface and an Odoo add-on. The repository also
includes translation-related Python services and a Docker Compose
configuration.

> **Project status:** Source code has been published to GitHub. The
> application has not been verified as fully runnable in this
> environment.

## Contents

-   [Architecture](#architecture)
-   [Technology stack](#technology-stack)
-   [Repository layout](#repository-layout)
-   [Prerequisites](#prerequisites)
-   [Configuration](#configuration)
-   [Run with Docker Compose](#run-with-docker-compose)
-   [Development notes](#development-notes)
-   [Security](#security)

## Architecture

The repository contains these components:

  ---------------------------------------------------------------------------
  Component               Directory                  Purpose
  ----------------------- -------------------------- ------------------------
  Authentication service  `auth-service/`            Authentication-related
                                                     API and user management

  Comments service        `comments-service/`        Comment-related API

  Documents service       `documents-service/`       Document management and
                                                     storage integration

  ESB service             `esb-service/`             Service orchestration

  Gateway service         `gateway-service/`         API gateway

  Web interface           `dms-ui/`                  React/Vite frontend

  Translator service      `translator-service/`      Python translation
                                                     service

  Translation writer      `translation-writer/`      Python component for
                                                     writing translation
                                                     output

  Odoo add-on             `odoo/addons/ensia_dms/`   DMS integration for Odoo

  Compose configuration   `docker-compose.yml`       Multi-container
                                                     development
                                                     configuration
  ---------------------------------------------------------------------------

The Java services use Maven project files (`pom.xml`). The frontend uses
npm/Vite, and the Python components include `requirements.txt` files.

## Technology stack

-   Java and Maven for backend services
-   Spring-based service components
-   React and Vite for the web interface
-   Python for translation-related components
-   Odoo add-on for integration
-   Docker Compose for container orchestration

## Repository layout

``` text
.
├── auth-service/
├── comments-service/
├── documents-service/
├── esb-service/
├── gateway-service/
├── dms-ui/
├── translator-service/
├── translation-writer/
├── odoo/
│   └── addons/
│       └── ensia_dms/
├── docker-compose.yml
├── test-all.sh
└── .gitignore
```

## Prerequisites

Depending on which components you want to work on, you may need:

-   Git
-   Docker and the Docker Compose plugin
-   Java Development Kit (JDK) compatible with the Maven project
    configuration
-   Maven, if building Java services outside containers
-   Node.js and npm, if working on the frontend outside containers
-   Python and pip, if working on the Python components outside
    containers
-   An Odoo environment, if developing the Odoo add-on

Check each component's configuration for its specific version and
dependency requirements.

## Configuration

1.  Clone the repository:

    ``` bash
    git clone https://github.com/chaima-m/DMS.git
    cd DMS
    ```

2.  Review `docker-compose.yml` and the service configuration files to
    identify required environment variables and dependencies.

3.  If the project requires a local `.env` file, create it from the
    configuration requirements documented by the project or its
    maintainers. Do not commit `.env` or real credentials to Git.

The repository's `.gitignore` excludes `.env` files and common generated
build artifacts.

## Run with Docker Compose

Review the Compose file and configure all required environment variables
and external dependencies before starting the stack.

``` bash
docker compose config
docker compose up --build
```

These commands are provided as a starting point; successful startup has
not been verified. Some services may require additional configuration or
infrastructure.

To stop the stack:

``` bash
docker compose down
```

## Development notes

-   Java services have their own Maven `pom.xml` files.
-   The frontend project is located in `dms-ui/`; inspect its
    `package.json` for available npm scripts.
-   Python dependencies are listed in the relevant `requirements.txt`
    files.
-   `test-all.sh` is included as a test helper. Review the script before
    running it and ensure its required services and dependencies are
    available.
-   Build outputs and dependency directories should remain untracked.

## Security

This is a public repository. Before adding changes, ensure that:

-   API keys, passwords, tokens, private keys, and production
    configuration are never committed.
-   Real credentials are stored locally or in an appropriate secrets
    manager.
-   Any sample configuration uses placeholder values only.
-   Dependencies and service configurations are reviewed before
    deploying the application.

## Repository

GitHub: <https://github.com/chaima-m/DMS>
