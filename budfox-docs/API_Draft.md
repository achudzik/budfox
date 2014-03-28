# API

## Resources

### Clients

- __POST /clients__ - create a new client
- __GET /clients__ - list all clients
- __PUT /clients__ - bulk update of given clients
- __DELETE /clients__ - delete all clients

- __GET /clients/{id}__ - get client identified by _{id}_
- __PUT /clients/{id}__ - if exists: update client identified by _{id}_,
        if not: error
- __DELETE /clients/{id}__ - delete client identified by _{id}_

- __GET /clients/{id}/loans__ - get all loans of client identified by _{id}_;
        

- __GET /clients?pesel={pesel}__ - get client with pesel equal given value


### Loans

- __POST /loans__ - create a new loan
- __GET /loans__ - list all loans
- __PUT /loans__ - bulk update of given loans
- __DELETE /loans__ - delete all loans

- __GET /loans/{id}__ - get loan identified by _{id}_
- __PUT /loans/{id}?extend=true__ - if exists: extend loan identified by _{id}_,
        if not: error
- __DELETE /loans/{id}__ - delete loan identified by _{id}_

- __GET /loans?client={clientId}__ - get all loans of client identified
        by _{clientId}_


## Versioning

By URL, as in /v1/resource. Only major version.


## Exceptions

Search-related resources returns empty collections instead of throwing 
exceptions.

TODO-ach

## Pagination

TODO-ach
