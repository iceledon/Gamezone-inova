# Team

## Members

| Name | Student Code | Role | Module | Feature Branch |
|---|---|---|---|---|
| Isabel Sofía Celedon Martinez | 1066872308 | Technical Lead | Sale + Console UI + Main | `feature/sale-module` |
| Jaime Alfonso Granados Diaz | 1067605475 | Developer 1 | Product | `feature/product-module` |
| Nicoll Jhoana Gómez Siosi | 1122840179 | Developer 2 | Person | `feature/person-module` |

## Class Distribution

### Technical Lead — Isabel Sofía Celedon Martinez

- `Sale`
- `SaleRepository`
- `SaleService`
- `ConsoleUI`
- `Main`

### Developer 1 — Jaime Alfonso Granados Diaz

- `Product` (abstract)
- `VideoGame`
- `Console`
- `ProductRepository`
- `ProductService`

### Developer 2 — Nicoll Jhoana Gómez Siosi

- `Person` (abstract)
- `Customer`
- `Seller`
- `PersonRepository`
- `PersonService`

## Committed Activities

### Technical Lead

1. Create the repository and configure `main` and `develop` with branch protection.
2. Configure the Maven project and the four-layer package structure.
3. Lead the analysis and produce the design documentation under `docs/`.
4. Author `TEAM.md` with roles, modules and activity commitments.
5. Implement the `Sale` class with its attributes, constructor and total calculation.
6. Implement `SaleRepository` with file-based persistence of sales.
7. Implement `SaleService` with the validation rules and the inventory update.
8. Implement `ConsoleUI` with the main menu and the three submenus.
9. Implement `Main` and wire the four layers together.
10. Review and merge the Pull Requests of both developers.
11. Run the end-to-end test of the ten operations and fix what it reveals.
12. Author the final `README.md` and deliver the repository.

### Developer 1

1. Create the `feature/product-module` branch.
2. Implement the abstract `Product` class with its common attributes.
3. Declare the abstract `getDescription()` method in `Product`.
4. Implement `decreaseStock(int)` in `Product`.
5. Implement the `VideoGame` subclass with its own description.
6. Implement the `Console` subclass with its own description.
7. Implement `ProductRepository` with save and load.
8. Implement `ProductService` with registration and listing.
9. Add stock update and product lookup to `ProductService`.
10. Write the JavaDoc of every class in the module.
11. Open the Pull Request towards `develop` and address the review.
12. Review the Pull Request of Developer 2.

### Developer 2

1. Create the `feature/person-module` branch.
2. Implement the abstract `Person` class with its common attributes.
3. Declare the abstract `getRoleDescription()` method in `Person`.
4. Implement the `Customer` subclass with its email attribute.
5. Implement the `Seller` subclass with employee code and shift.
6. Implement `PersonRepository` for customer persistence.
7. Implement `PersonRepository` for seller persistence.
8. Implement `PersonService` with registration and listing.
9. Add customer and seller lookup by id to `PersonService`.
10. Create `data/sellers.txt` with the three preloaded sellers.
11. Open the Pull Request towards `develop` and address the review.
12. Review the Pull Request of Developer 1.

## Review Rules

Nobody approves their own Pull Request. Developer 1 and Developer 2 review each other; the
Technical Lead reviews both, and one of the two developers reviews the Technical Lead's Pull
Request.
