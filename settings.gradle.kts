
rootProject.name = "self-hr"

include(
    "backend",
    "backend:api",
    "backend:applications",
    "backend:core",
    "backend:testCore",
    "backend:deprecateddomains",
    "backend:domains:proprietor",
    "backend:domains:attendance",
    "backend:domains:contract",
    "backend:domains:project",
    "backend:domains:invoice",
    "backend:infrastructure",
    "backend:shared",
)
