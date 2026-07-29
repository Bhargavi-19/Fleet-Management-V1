# FLEMAN — Project Changes

**Last updated:** 29 July 2026

This document covers five phases of work:

| Phase | Scope |
|---|---|
| **Phase 1** | Frontend/backend integration up to the **Return** workflow |
| **Phase 2** | Modify Booking · Vehicle Info · My Information · Booking-time profile capture · **Invoice module** · Email after return · AOP logging · **Google SSO** · **Excel upload** |
| **Phase 3** | Bug fixes and enhancements — Modify Booking button, structured address, car-type-aware vehicle assignment, **staff Invoice module**, modern HTML e-mail, redesigned invoice PDF. **See §18 onwards.** |
| **Phase 4** | Past-date blocking · booking e-mail & PDF rebranded to match the invoice · shorter T&Cs. **See §29.** |
| **Phase 5** | **Pick-up vs drop-off hub workflow** — one-way rentals now route correctly between hubs. **See §30.** |

Everything requested is now implemented. Two verification steps remain and are
listed in §15 — they need a machine with JDK 17, Maven and MySQL.

---

## 1. Complete application flow

### 1.1 Technology

| | |
|---|---|
| **Backend** | Spring Boot 4.1 · Java 17 · Spring Data JPA · Spring Security (JWT + OAuth2) · MySQL · OpenPDF · Apache POI · AspectJ |
| **Frontend** | React 19 · TypeScript · Vite · Tailwind v4 · Recharts · lucide-react |
| **Backend path** | `Fleet-Management-V1/backend` |
| **Frontend path** | `claude-frontend/fleman-frontend` |

### 1.2 Customer journey

```
Landing / CustomerHome
   │  state → city (searchable) OR airport code, plus dates
   ▼
Location step        (skipped entirely when both hubs are already known)
   │  GET /api/states · /api/cities/state/{id} · /api/hubs?stateId&cityId
   ▼
Vehicle step         GET /api/car-types/{hubId}      → choose a CAR TYPE
   ▼
Add-ons              GET /api/addons/{hubId}
   ▼
Your Info            guests also give state / city / pincode
   ▼
Confirm
   │  signed in → POST /api/bookings      (licence + passport saved to profile)
   │  guest     → POST /api/bookings/guest
   ▼
Booking = PENDING · confirmation e-mail + PDF sent in the background
   ▼
Customer Dashboard
   ├── Dashboard      stats · next rental · recent bookings
   ├── My Information view + EDIT  (PUT /api/customer/profile)
   ├── My Bookings    view · MODIFY (reprices) · cancel
   ├── Invoices       view + download PDF
   ├── Change Password
   └── Support
```

### 1.3 Staff journey

```
Login (Staff tab)  POST /api/staff/login → token + hubId
   ▼
Dashboard · Bookings · Vehicles
   ▼
HAND-OVER   GET /api/staff/bookings?status=PENDING
   │  assign a car   POST /api/staff/handover/assign-vehicle
   │  fuel out       POST /api/staff/handover/confirm
   ▼  booking → CONFIRMED · car → RENTED · invoice row created
RETURN      GET /api/staff/bookings?status=CONFIRMED
   │  fuel in + fuel charges   POST /api/staff/handover/return
   ▼  booking → COMPLETED · car → AVAILABLE
      invoice NUMBERED and DATED · PDF generated · e-mailed to the customer
EXCEL UPLOAD  bulk vehicle rate data for the staff member's own hub
```

### 1.4 Request/response flow

```
Controller → Service (interface) → ServiceImpl → Repository → MySQL
     ↑                                                          │
     └────────── ApiResponse<T> { success, message, data } ──────┘

JwtFilter → JwtService → CustomUserDetailsService → ROLE_STAFF | ROLE_CUSTOMER
OAuth2SuccessHandler issues the same JWT for Google sign-in.
GlobalExceptionHandler maps every exception onto the same envelope.
LoggingAspect wraps the customer-registration controller and service.
```

---

## 2. List of all modified files

### 2.1 Backend — new files

| File | Purpose |
|---|---|
| `aspect/LoggingAspect.java` | AOP logging (F9) |
| `controller/InvoiceController.java` | Invoice JSON + PDF download |
| `controller/ExcelUploadController.java` | Rate upload + template |
| `service/InvoiceService.java` · `impl/InvoiceServiceImpl.java` | Invoice retrieval, ownership checks |
| `service/ExcelUploadService.java` · `impl/ExcelUploadServiceImpl.java` | POI parsing, validation, upsert |
| `dto/response/InvoiceResponse.java` | Full invoice DTO |
| `dto/response/InvoiceAddonResponse.java` | Invoice add-on line |
| `dto/response/ExcelUploadResultResponse.java` | Upload summary + per-row errors |
| `dto/response/BookingAddonResponse.java` | Booking add-on line (Phase 1) |
| `exception/error/BusinessException.java` | → HTTP 400 (Phase 1) |
| `exception/error/UnauthorizedActionException.java` | → HTTP 403 (Phase 1) |
| `resources/db/schema-updates.sql` | Manual `ALTER TABLE`s (Phase 1) |
| `resources/db/seed-data.sql` | Real master data + demo accounts |

### 2.2 Backend — modified files

| File | Change |
|---|---|
| `pom.xml` | + `oauth2-client`, `poi-ooxml`, `aspectjweaver` |
| `BackendApplication.java` | `@EnableAsync` |
| `security/SecurityConfig.java` | Public master data, guest booking, OAuth2 login, staff/customer ordering |
| `security/OAuth2SuccessHandler.java` | Rewritten — auto-provisioning, staff support, configurable redirect |
| `config/Cors_Config.java` | Ports 5173 + 5174, `PATCH` |
| `entity/base/Customer.java` | `city`/`state` nullable; + `dateOfBirth`, `gender`, `nationality` |
| `entity/BookingHeader.java` | `car_id` nullable |
| `entity/InvoiceHeader.java` | + `invoiceNo`, `invoiceDate`, `returnDate`, address snapshot |
| `service/impl/BookingServiceImpl.java` | Shared mapper · car type name · profile capture · **server-side repricing** |
| `service/impl/CustomerServiceImpl.java` | Consistent envelopes · partial profile update · duplicate → 400 |
| `service/impl/HandoverServiceImpl.java` | Invoice numbering, return details, invoice e-mail |
| `service/impl/EmailServiceImpl.java` | `@Async`, non-fatal failures, `sendInvoice` |
| `service/impl/PdfServiceImpl.java` | + `generateInvoicePdf` |
| `service/impl/CarServiceImpl.java` | Optional status filter |
| `exception/error/GlobalExceptionHandler.java` | Proper status codes |
| `controller/{Customer,Booking,StaffBooking,Car}Controller.java` | `@Valid`, envelopes, status filter, removed broken endpoint |
| `repository/{Car,CarType}Repository.java` | `findByHub_HubIdAndStatus`, `findByHub_HubIdAndCarClass` |
| `dto/request/*` | Validation annotations; `carTypeId`, licence/passport on booking |
| `dto/response/{Booking,Profile}Response.java` | Many new fields |
| `resources/application.properties` | Mail timeouts, multipart limits, Google OAuth2, redirect URI |

### 2.3 Frontend — new files

`src/api/{client,types,index}.ts` (replaces `src/api.ts`) ·
`src/components/SearchSelect.tsx` ·
`src/screens/OAuthSuccess.tsx` ·
`src/screens/staff/ExcelUpload.tsx`

### 2.4 Frontend — modified files

`App.tsx` · `data.ts` · `components/Layout.tsx` · `components/shared.tsx` ·
`screens/Auth.tsx` · `BookingFlow.tsx` · `CustomerDashboard.tsx` ·
`CustomerHome.tsx` · `Landing.tsx` · `staff/{Dashboard,Bookings,Vehicles,Operations}.tsx` ·
`vite.config.ts`

**Untouched:** `screens/admin/*` (mock only, no backend), `Modify.tsx`, `Membership.tsx`.

---

## 3. Backend changes

### Modify Booking (F1)
`updateBooking` now:
- accepts `carTypeId`, so the customer can change vehicle category;
- **recalculates every amount from the database** — the browser's totals are ignored;
- verifies the car type is actually offered at the chosen pick-up hub;
- derives `duration` from the dates instead of trusting it;
- rejects an end date not after the start, and a start date in the past;
- **refuses to modify once a vehicle is allocated** (`assignedCarId`/`handoverDate` set);
- rewrites `booking_detail` lines with names and prices read from `addon`.

Pricing uses the same tiering as the booking screen:
`40 days = 1 month + 1 week + 3 days`, then 18% GST.

### Vehicle Information (F2)
`BookingResponse` gained `carTypeName`, `carClass`, `carTypeImageUrl`, resolved
through `CarTypeRepository` in the shared mapper.

### My Information (F3)
No new endpoint — the existing `PUT /api/customer/profile` is reused. It applies
only non-null fields, so partial updates never wipe other data, and rejects a
phone number already registered to someone else.

### Booking-time profile capture (F4)
`BookingRequest` gained `drivingLicenseNo` / `passportNo`. `saveDocumentsOnProfile`
copies them onto the `Customer` row when supplied; blanks never overwrite.

### Invoice (F5) and Email (F6)
See §10 and §11.

### AOP (F9), SSO (F10), Excel (F11)
See §12, §13, §14.

---

## 4. Frontend changes

- **Customer Dashboard** — new **Dashboard** overview tab (stats, next rental, recent bookings, quick actions) and new **Invoices** tab.
- **My Information** — view/edit toggle wired to the existing update API; email disabled with an explanation.
- **Modify Booking** — Vehicle Type dropdown, live repricing that mirrors the server, existing add-ons pre-selected (they previously reset to empty and were silently dropped).
- **Vehicle Information** — leads with the booked category and image; shows "*Your exact vehicle is allocated at pick-up*" until hand-over.
- **Invoice page** — full invoice view plus **Download PDF** from both the list and the modal.
- **Excel Upload screen** — drag-and-drop, template download, column reference, result counters and a table of rejected rows.
- **OAuth callback** — `/oauth-success` stores the token and scrubs it from the address bar.
- **SearchSelect** — reusable type-to-search dropdown used for state/city everywhere.
- Loading skeletons, error banners with retry, success toasts and inline validation throughout.

---

## 5. Database changes

| Table | Change | Applied by |
|---|---|---|
| `customer` | `city_id`, `state_id` → **nullable** | `schema-updates.sql` (manual) |
| `customer` | + `date_of_birth`, `gender`, `nationality` | Hibernate |
| `booking_header` | `car_id` → **nullable** | `schema-updates.sql` (manual) |
| `invoice_header` | + `invoice_no` (unique), `invoice_date`, `return_date`, `address_line_1/2`, `city_id`, `state_id`, `pincode` | Hibernate |

> `ddl-auto=update` adds columns but never relaxes an existing `NOT NULL`,
> which is why the two nullability changes need the manual script.

---

## 6. API changes

### New endpoints

| Method | Path | Role | Purpose |
|---|---|---|---|
| `GET` | `/api/invoices/booking/{bookingId}` | CUSTOMER | Invoice as JSON |
| `GET` | `/api/invoices/booking/{bookingId}/download` | CUSTOMER | Invoice PDF |
| `POST` | `/api/staff/excel/car-types` | STAFF | Upload rate sheet (multipart) |
| `GET` | `/api/staff/excel/car-types/template` | STAFF | Blank `.xlsx` template |
| `GET` | `/oauth2/authorization/google` | public | Start Google sign-in |
| `GET` | `/login/oauth2/code/google` | public | Google callback |

### Changed endpoints

| Endpoint | Change |
|---|---|
| `POST /api/bookings` | + `drivingLicenseNo`, `passportNo`; `carTypeId` required, `carId` no longer expected |
| `PUT /api/bookings/{id}` | + `carTypeId` (required); **amounts recalculated server-side**; new date and status rules |
| `GET /api/bookings*` | + `carTypeName`, `carClass`, `carTypeImageUrl`, assigned vehicle, fuel, rates, `addons` |
| `POST /api/bookings/guest` | Now public; `stateId`, `cityId`, `drivingLicenseNo` required |
| `GET|PUT /api/customer/profile` | Wrapped in `ApiResponse`; 10 extra fields; partial updates |
| `POST /api/customer/login` | Wrapped in `ApiResponse`; bad credentials → 401 |
| `POST /api/customer/register` | Duplicate email/phone → **400** (was 200 with `success:false`) |
| `GET /api/cars/hub/{hubId}` | Optional `?status=` |
| `PATCH /api/bookings/{id}/status` | **Removed** — did not compile and was a privilege-escalation risk |
| Master data GETs | Now public (needed before login) |
| *All* | 400/401/403/404 with real messages instead of blanket 500 |

---

## 7. Entity changes

- **`Customer`** — `city`/`state` optional; + `dateOfBirth`, `gender`, `nationality`.
- **`BookingHeader`** — `car_id` nullable (customer books a *type*; staff assign the car).
- **`InvoiceHeader`** — + `invoiceNo`, `invoiceDate`, `returnDate`, address snapshot.
- **`CarType`**, **`Car`**, **`Addon`**, **`Staff`**, **`Hub`** — unchanged.

---

## 8. DTO changes

**New:** `InvoiceResponse`, `InvoiceAddonResponse`, `ExcelUploadResultResponse`
(+ nested `RowError`), `BookingAddonResponse`.

**Changed:**

| DTO | Change |
|---|---|
| `BookingRequest` | + `carTypeId` (required), `drivingLicenseNo`, `passportNo`; validation |
| `UpdateBookingRequest` | + `carTypeId` (required); amounts now advisory only |
| `GuestBookingRequest` | Validation; `@Valid` on nested booking |
| `RegisterRequest`, `ChangePasswordRequest`, `UpdateBookingStatusRequest` | Validation |
| `UpdateCustomerRequest` | Rewritten — every field optional, + address/DOB/gender/nationality |
| `BookingResponse` | + car type info, assigned vehicle, fuel, rates, add-ons, phone, licence |
| `ProfileResponse` | + DOB, gender, nationality, address, city/state names, pincode, document type |

---

## 9. New endpoints

Listed in §6.

---

## 10. Invoice generation flow

```
HAND-OVER
  confirmHandover()
    └─ creates invoice_header + invoice_detail rows
       (customer, vehicle, hub, dates, charges, address, fuel out)

RETURN
  returnVehicle()
    ├─ booking → COMPLETED, car → AVAILABLE
    ├─ invoice.fuelLevelIn / fuelCharges / returnDate / invoiceDate
    ├─ invoiceNo = INV-{year}-{6-digit invoiceId}      ← assigned once only
    ├─ invoiceService.loadInvoiceForBooking(bookingId)
    └─ emailService.sendInvoice(invoice)               ← background thread

CUSTOMER
  GET /api/invoices/booking/{id}            → Invoices tab
  GET /api/invoices/booking/{id}/download   → PDF
```

The PDF contains: company name/address/GSTIN, **invoice number**, invoice date,
booking reference, billed-to block (name, e-mail, phone, address, licence,
passport), pick-up and drop-off hubs, hand-over and return timestamps, rental
period and duration, vehicle category/registration/model, fuel out and in, an
itemised charges table (vehicle rental with rate and days, **each add-on by
name**, add-ons subtotal, GST 18%, booking total, fuel charges) and
**TOTAL PAYABLE**.

`finalAmount = grandTotal + fuelCharges`. The booking total is left untouched so
the originally agreed price stays visible next to the final one.

---

## 11. Email flow

| Trigger | Mail | Attachment |
|---|---|---|
| Booking created | Confirmation | Booking PDF |
| Vehicle returned | Invoice | Invoice PDF |

Both run on a **background thread** (`@EnableAsync` + `@Async`) and both swallow
and log failures. Rationale:

- The booking/return is already committed, so a mail problem must never undo it.
- JavaMail has **no default timeout**; sending inline made *Confirm Booking* hang
  indefinitely when SMTP was slow. Connect/read/write timeouts are now 5s.
- Failures appear as `Could not send … : <reason>` in the log.

---

## 12. AOP implementation

**File:** `aspect/LoggingAspect.java` — heavily commented as a learning reference.

**Applied to one module only** so the log stays readable:

```
POST /api/customer/register
  → CustomerController.register(..)     @Before / @After
  → CustomerServiceImpl.register(..)    @Around (times it)
  exceptions from either                @AfterThrowing
```

**Sample output**

```
-> ENTER  CustomerController.register() args=[RegisterRequest]
-> ENTER  CustomerServiceImpl.register()
<- EXIT   CustomerServiceImpl.register() took 118 ms
<- EXIT   CustomerController.register()
```

**How it works.** Spring never edits your class; at startup it wraps the bean in
a **proxy**, and everyone gets the proxy injected. The proxy runs the advice and
then the real method. A consequence worth remembering: a method calling another
method *on itself* bypasses the proxy, so no advice runs — the same rule applies
to `@Transactional` and `@Async`.

**Vocabulary:** *Aspect* (the class) · *Join point* (a method call) ·
*Pointcut* (which methods) · *Advice* (what to run: `@Before`, `@After`,
`@AfterThrowing`, `@Around`).

Only `@Around` can measure duration, because only it decides when the real
method runs (`joinPoint.proceed()`).

**Security note:** the aspect logs argument **types**, never values — the
registration payload contains a raw password.

**To widen it:** change the pointcut to `within(com.example.demo.controller..*)`.

---

## 13. SSO implementation

**Starting point (yours):** `oauth2Login()` wired to an `OAuth2SuccessHandler`
that issued a JWT for an existing customer and otherwise returned
`401 "User is not registered."`

**Completed:**

1. `spring-boot-starter-oauth2-client` added; Google client id/secret read from
   `GOOGLE_CLIENT_ID` / `GOOGLE_CLIENT_SECRET`.
2. Merged into the *current* `SecurityConfig` without losing the public-endpoint
   work; `/oauth2/**` and `/login/oauth2/**` permitted.
3. **Staff are checked first**, matching `CustomUserDetailsService` ordering.
4. **First-time Google users are auto-provisioned** — previously they hit a dead
   end, which defeats the purpose of SSO.
5. Redirect target is configurable (`app.oauth2.redirect-uri`) instead of a
   hard-coded `localhost:5173`, and the token is URL-encoded.
6. Frontend: `/oauth-success` screen stores the token exactly as a password
   login would, loads the profile, and **removes the token from the address bar**
   so it does not linger in browser history. The existing "Continue with Google"
   button is wired; Vite proxies `/oauth2` and `/login/oauth2`.

```
Browser → /oauth2/authorization/google → Google → /login/oauth2/code/google
        → OAuth2SuccessHandler (JWT) → /oauth-success?token=…&role=…
        → token stored → dashboard
```

**Setup:** create an OAuth client at Google Cloud Console → Credentials, with
authorised redirect URI exactly `http://localhost:8080/login/oauth2/code/google`,
then uncomment the three `spring.security.oauth2.client.registration.google.*`
lines (ideally in `application-local.properties`).

**Google sign-in is optional at runtime.** `SecurityConfig` injects
`ObjectProvider<ClientRegistrationRepository>` and only calls `.oauth2Login(...)`
when that bean exists, so the application starts normally with no credentials —
the Google button is simply inert.

> A blank `client-id` is **not** the same as an absent one. Defining
> `...google.client-id=` with an empty value makes Spring build a registration
> and fail at startup with *"clientId cannot be empty"*, which is why those
> properties ship commented out rather than defaulted to `""`.

---

## 14. Excel upload implementation

**BRD 3.5:** *"The system should provide user to upload vehicle rate data in bulk
thru' Excel. Excel format to enter the data should be provided."*

**Columns** (row 1 header, data from row 2):

| | Column | Rule |
|---|---|---|
| A | Car Class | `SMALL` / `COMPACT` / `INTERMEDIATE` / `SEDAN` / `SUV` |
| B | Car Type | required, ≤100 chars |
| C–E | Daily / Weekly / Monthly Rate | numeric, > 0 |
| F–G | Effective From / To | `yyyy-MM-dd` or a real Excel date; To ≥ From |
| H | Image URL | optional |

**Behaviour**

- Rates always apply to the **uploading staff member's own hub**.
- **Upsert:** one rate row per car class per hub. Re-uploading a corrected sheet
  updates rather than duplicating.
- **A bad row never stops the file.** Each row is validated independently; valid
  rows save and rejected rows come back with their spreadsheet row number and
  reason.
- Sanity checks flag weekly > 7×daily and monthly > 30×daily (almost always a
  typo).
- File guards: `.xlsx` only, 2 MB cap, empty-sheet rejection, plus Spring
  multipart limits.
- Blank trailing rows are ignored; numeric cells never render as `1800.0`.

**UI:** drag-and-drop, *Download template*, column reference, and a result panel
with rows-read / added / updated / rejected plus the error table.

---

## 15. Testing checklist

### Build (must be run by you — see §16)
- [ ] `./mvnw clean compile` succeeds
- [ ] `npm run build` succeeds
- [ ] `npx tsc --noEmit` — **already verified clean**

### Setup
- [ ] Run `schema-updates.sql` on an existing DB (not needed for a fresh one)
- [ ] Run `seed-data.sql`
- [ ] Set Google client id/secret if testing SSO

### Customer
- [ ] Register → duplicate e-mail returns a clear 400
- [ ] Login; **Google sign-in** creates an account first time
- [ ] Book by airport code (no location step) and by state+city (hub picker)
- [ ] Guest booking asks for state/city and succeeds
- [ ] Licence/passport typed at booking appear on **My Information**
- [ ] Dashboard shows stats, next rental, recent bookings
- [ ] **Modify booking**: change vehicle type → total changes; dates change → duration and total change; add-ons stay ticked
- [ ] Modify rejects past start date, end ≤ start, and a booking already handed over
- [ ] Edit profile saves; email field disabled
- [ ] Change password works

### Staff
- [ ] Staff login lands on the hub dashboard
- [ ] Hand-over: assign vehicle → fuel out → confirm (car → RENTED)
- [ ] Return: fuel in + charges → COMPLETED, car → AVAILABLE
- [ ] **Invoice e-mail arrives with the PDF attached**
- [ ] **Excel upload**: template downloads; a good sheet inserts then updates; a sheet with a bad car class, zero rate and reversed dates reports exactly those rows and saves the rest

### Invoice
- [ ] Invoices tab lists completed rentals
- [ ] View shows number, dates, vehicle, itemised charges, total payable
- [ ] Download produces a readable PDF
- [ ] Another customer's booking id returns 403

### Cross-cutting
- [ ] `register` logs ENTER/EXIT with timing, and no password in the log
- [ ] Stopping the backend gives "Unable to reach the server", not a hang

---

## 16. Assumptions, known limitations, pending work

### Assumptions
1. Customer books a **car type**; staff assign the actual car at hand-over (BRD).
2. Guest booking stays and is public; guests supply their own address.
3. The invoice row is created at hand-over but **numbered and dated at return**.
4. `finalAmount = grandTotal + fuelCharges`; the booking total is preserved.
5. Excel rates apply to the uploader's hub, one row per car class per hub.
6. First-time Google users are auto-created as customers.
7. Google gives no phone number, and `phone` is `NOT NULL UNIQUE`, so a
   placeholder starting `0` is stored (invalid for real Indian mobiles, so it
   cannot collide) for the customer to replace on My Information.
8. AOP is scoped to registration for learning clarity.
9. Admin screens are mock-only and hidden — the backend has no `ROLE_ADMIN`.

### Known limitations
1. **`POST /api/bookings` still trusts the browser's totals.** Only *modify*
   reprices server-side. Applying the same `calculateVehicleAmount` /
   `calculateAddonAmount` helpers to creation is a small, high-value change.
2. **N+1 queries** — the booking mapper fetches add-ons and car type per row.
   Fine at current volume; batch-fetch when lists grow.
3. **No `HANDED_OVER` status** — `CONFIRMED` doubles as "vehicle is out".
4. **A car can be assigned twice** — `assignVehicle` does not reserve it; only
   `confirmHandover` sets `RENTED`.
5. **Master data is publicly readable**, including `/api/cars/**`.
6. Staff booking search is client-side over the loaded page only.
7. BRD invoice fields **odometer_out/in** and **car_condition_out/in** are not
   captured — the return screen would need new inputs.
8. `invoice_detail` keeps one row per add-on rather than the BRD's single
   totals row; this preserved the existing working code.
9. Excel upload covers **rates only**, not vehicles or customers.
10. **No automated tests.**
11. `application-local.properties` holds live credentials — confirm it is
    git-ignored and rotate if it was ever committed.
12. `.metadata/`, `bin/`, `src/bin/` (Eclipse artefacts) are still in the repo.

### Pending work
- Excel upload for **vehicles** and **customers**, if wanted beyond BRD 3.5.
- Admin module (no backend at all today).
- Membership registration (BRD 3.4) — screen exists, no backend.
- Document upload/verification (`document_type`, `file_url` exist unused).
- Support/contact form is static.

### Recommendations
1. **Reprice on create as well as modify**, and reject mismatched client totals.
2. Add a `HANDED_OVER` status and reserve the car at assignment.
3. Adopt **Flyway/Liquibase** and turn off `ddl-auto=update` — the manual
   `ALTER TABLE` step in §5 is exactly what migrations solve.
4. Introduce **React Router**; screen state is a single `useState` string, so
   there are no URLs, no back button and no deep links.
5. Move the JWT secret to an environment variable; add refresh tokens.
6. Tests: `@DataJpaTest` for repositories, `@WebMvcTest` for controllers, and a
   smoke test over book → hand-over → return → invoice.
7. Extract a `useApi` hook — the loading/error/retry triple repeats in every screen.

---

## 17. Verification status

| Check | Result |
|---|---|
| TypeScript (`npx tsc --noEmit`) | ✅ **0 errors** |
| Every `ServiceImpl` satisfies its interface | ✅ |
| No unresolved method calls across all Java sources | ✅ |
| Brace balance in all Java files | ✅ |
| Frontend API paths match real controller mappings | ✅ |
| `./mvnw clean compile` | ⚠️ **not run here** — no JDK 17/Maven in this environment |
| `npm run build` | ⚠️ **not run here** — `node_modules` holds only the Windows Rolldown binary |
| End-to-end against MySQL | ⚠️ **not run here** |

Two dependency notes:

- `spring-boot-starter-aop` **does not exist in Spring Boot 4** — replaced with
  `org.aspectj:aspectjweaver`. (Boot 4 also renamed `starter-web` →
  `starter-webmvc`, which this project already uses.)
- If `spring-boot-starter-oauth2-client` fails to resolve the same way, replace
  it with `org.springframework.security:spring-security-oauth2-client` plus
  `spring-security-oauth2-jose` at an explicit version.

---
---

# Phase 3 — Bug fixes & enhancements

**Date:** 29 July 2026

---

## 18. Bug fixes

### 18.1 Modify Booking did nothing (root cause)

`ManageBookingModal` gated the button on:

```js
const canModify = data && data.bookingStatus === 'CONFIRMED'
```

`CONFIRMED` means the vehicle has **already been handed over**, and the backend
rule added in Phase 2 explicitly refuses to modify a booking once a car is
allocated. So the button was enabled *only* for bookings the server would
reject, and **disabled for `PENDING` bookings — the only ones that can actually
be modified.** The two rules were exact opposites.

**Fix** — the UI now mirrors the server exactly:

```js
const canModify = data.bookingStatus === 'PENDING'
  && !data.assignedCarId
  && !data.handoverDate
```

A disabled button now also explains itself ("A vehicle has already been
allocated for this booking…") instead of sitting there dead.

Also fixed while in there: opening edit mode reset the add-on selection to
empty, so saving silently dropped every add-on. Existing add-ons are now
pre-ticked from `booking.addons`.

### 18.2 Invoices missing from the Staff Invoice section

The invoices were being **stored correctly all along**. The Staff → Invoices
screen was still the "coming soon" placeholder from Phase 2, and there was no
staff-facing endpoint. Both now exist (§21).

### 18.3 Wrong-category vehicle could be handed over

Hand-over listed *every* available car at the hub, so staff could hand over a
hatchback for an SUV booking. Now filtered by the booked car type, **and
rejected server-side** (§20).

---

## 19. Structured address

The database already stored `address_line_1`, `address_line_2`, `city_id`,
`state_id` and `pincode` as separate columns — the problem was that they were
never exposed or displayed consistently.

**Backend**
- `BookingResponse` gained `addressLine1`, `addressLine2`, `cityId`, `cityName`,
  `stateId`, `stateName`, `pincode`, with city and state resolved to names.
- `InvoiceResponse` gained `cityName` and `stateName`.
- `PdfServiceImpl` builds the address from the structured parts via a small
  `appendPart` helper, so blanks never leave stray commas.

**Frontend**
- New `src/utils/address.ts` — `formatAddress()`, `addressLines()`,
  `hasAddress()`. One implementation, used by My Information, the customer
  invoice modal and the staff invoice modal.
- The guest booking form's single "Home Address" box is now **Address Line 1**
  and **Address Line 2**, matching the profile form and the database.

No schema change was needed.

---

## 20. Vehicle assignment logic

```
Staff opens Hand-over
   └─ picks a PENDING booking
        └─ GET /api/cars/hub/{pickupHubId}/car-type/{carTypeId}?status=AVAILABLE
             ├─ cars found  -> listed, with a "Booked category: …" chip
             └─ none found  -> amber message explaining what to do next
   └─ POST /api/staff/handover/assign-vehicle
        └─ server re-checks: car exists · belongs to this hub · is AVAILABLE
           · AND its car type matches booking.carTypeId
```

The category check lives in `HandoverServiceImpl.assignVehicle`, so it holds
even if the API is called directly. The screen filter only saves a wasted
attempt.

**New:** `CarRepository.findByHub_HubIdAndCarType_CarTypeIdAndStatus(...)` and a
`?status=` parameter on `GET /api/cars/hub/{hubId}/car-type/{carTypeId}`.

---

## 21. Staff Invoice module

**New endpoints** (all `ROLE_STAFF`, all scoped to the staff member's hub):

| Method | Path | Purpose |
|---|---|---|
| `GET` | `/api/staff/invoices?page&size&search` | Paginated list |
| `GET` | `/api/staff/invoices/{invoiceId}` | One invoice |
| `GET` | `/api/staff/invoices/{invoiceId}/download` | PDF |

`search` matches invoice number, booking id, customer name, e-mail or vehicle
registration, via a single JPQL query — no in-memory filtering.

**New files:** `StaffInvoiceController.java`, `InvoicePageResponse.java`,
`screens/staff/Invoices.tsx`.

The screen has a debounced search box, pagination, a detail modal showing the
full charge breakdown, and PDF download from both the row and the modal.
`InvoiceHeaderRepository.searchByHub(...)` backs it.

---

## 22. Modify Booking flow

```
My Bookings -> Manage (PENDING or CONFIRMED)
   └─ Modify Booking            enabled only when PENDING + no vehicle allocated
        ├─ form prefilled from the booking: dates, hubs, car type, add-ons
        ├─ Vehicle Type dropdown lists the categories at that hub with rates
        ├─ live total recalculated with the same monthly/weekly/daily tiering
        │  the server uses
        └─ Save -> PUT /api/bookings/{id}
             ├─ status + allocation re-checked
             ├─ dates validated, duration derived from them
             ├─ car type verified against the pick-up hub
             ├─ EVERY amount recalculated from the database rate card
             └─ booking_detail lines rewritten with DB names and prices
```

---

## 23. Email template improvements

`EmailTemplates.java` builds the post-return e-mail as responsive HTML:

- Branded header (FLEMAN, invoice number)
- Personal greeting
- Amount banner with a green **PAID** pill
- Three detail cards: booking, vehicle, return summary
- Full payment summary including each add-on by name
- "Your invoice is attached" note pointing at the Invoices tab
- Footer with company address, phone, e-mail and GSTIN
- Hidden preview text for the inbox list

**Why the markup looks dated:** e-mail clients are not browsers. Outlook renders
with Word, Gmail strips `<style>` blocks, and flexbox/grid are unreliable. So it
uses table layout, inline styles on every element, a 600px container and
web-safe fonts — the standard approach that actually renders in Gmail, Outlook,
Apple Mail and mobile clients.

---

## 24. PDF design improvements

Building on the Phase 2 layout, the invoice PDF now also has:

- **PAYMENT STATUS: PAID IN FULL** banner on a green background, with the
  settlement timestamp
- **Terms & Conditions** — seven numbered clauses covering rates, fuel, late
  return, fines, damage, GST and the dispute window
- Branded footer repeating company name, address, contact and GSTIN
- Address assembled from the structured fields
- Colours matched to the e-mail template (`#2563EB` brand, slate greys)

Already present from Phase 2: company header, invoice number and date, billed-to
block, hub and date details, vehicle block, itemised charges with each add-on,
GST line, fuel charges and **TOTAL PAYABLE**.

---

## 25. Phase 3 — files changed

### Backend — new
`controller/StaffInvoiceController.java` ·
`dto/response/InvoicePageResponse.java` ·
`service/impl/EmailTemplates.java`

### Backend — modified
| File | Change |
|---|---|
| `service/impl/HandoverServiceImpl.java` | Car type must match the booking |
| `service/impl/InvoiceServiceImpl.java` | Staff list/get/download, `decorate()` helper, city/state names |
| `service/InvoiceService.java` | Three staff methods |
| `repository/InvoiceHeaderRepository.java` | `searchByHub`, `countByPickupHubId` |
| `repository/CarRepository.java` | `findByHub_HubIdAndCarType_CarTypeIdAndStatus` |
| `service/CarService.java`, `impl/CarServiceImpl.java`, `controller/CarController.java` | Optional status on the car-type endpoint |
| `service/impl/BookingServiceImpl.java` | Structured address on every booking response |
| `dto/response/BookingResponse.java` | 7 address fields |
| `dto/response/InvoiceResponse.java` | `cityName`, `stateName` |
| `service/impl/EmailServiceImpl.java` | HTML body via `EmailTemplates` |
| `service/impl/PdfServiceImpl.java` | Payment status, T&Cs, footer, address helper |

### Frontend — new
`screens/staff/Invoices.tsx` · `utils/address.ts`

### Frontend — modified
`App.tsx` · `components/Layout.tsx` · `screens/CustomerDashboard.tsx` ·
`screens/BookingFlow.tsx` · `screens/staff/Operations.tsx` ·
`api/index.ts` · `api/types.ts`

---

## 26. Phase 3 — testing checklist

### Modify Booking
- [ ] A **PENDING** booking shows an enabled **Modify Booking** button
- [ ] A **CONFIRMED** (handed-over) booking shows it disabled, with the reason underneath
- [ ] Cancelled / completed bookings likewise explain themselves
- [ ] Edit mode prefills dates, hubs, car type and **existing add-ons stay ticked**
- [ ] Changing the vehicle type changes the live total
- [ ] Saving persists to the database and the list refreshes
- [ ] Past start date, end ≤ start, and a car type not offered at the hub are all rejected

### Address
- [ ] Guest booking form shows Address Line 1 and Line 2 separately
- [ ] My Information saves and redisplays the full structured address
- [ ] The same formatting appears on the profile, customer invoice, staff invoice and PDF

### Vehicle assignment
- [ ] Hand-over lists **only** available cars of the booked category
- [ ] The "Booked category: …" chip shows the right car type
- [ ] With no matching car free, the amber message appears and assignment is blocked
- [ ] Calling `assign-vehicle` directly with a mismatched `carId` returns 400

### Staff invoices
- [ ] Complete a return, then the invoice appears in Staff → Invoices
- [ ] Search by invoice number, booking id, customer name, e-mail and registration
- [ ] Pagination works past 10 invoices
- [ ] View shows the full breakdown; Download produces a readable PDF
- [ ] An invoice from another hub returns 403

### Email & PDF
- [ ] Return e-mail arrives as formatted HTML, not raw tags
- [ ] Renders correctly in Gmail (web + mobile) and Outlook
- [ ] Amount banner, PAID pill and all three cards populate
- [ ] PDF shows PAID IN FULL, Terms & Conditions and the branded footer
- [ ] PDF prints cleanly on A4

---

## 27. Phase 3 — assumptions & remaining work

### Assumptions
1. A booking is modifiable only while `PENDING` **and** unallocated. Changing an
   in-progress rental is a hub desk operation, not self-service.
2. Payment status is shown as **PAID** — the project has no payment gateway, so
   a completed return is treated as settled. Wire this to a real payment record
   when one exists.
3. Company details (address, phone, GSTIN) are placeholders in
   `EmailTemplates` and `PdfServiceImpl`. Replace with the real registered
   details before going live.
4. Staff invoice search is a single JPQL `LIKE` query — fine at this scale; add
   an index on `invoice_no` if volumes grow.

### Remaining work
1. **`POST /api/bookings` still trusts the browser's totals.** Only *modify*
   reprices server-side. The `calculateVehicleAmount` / `calculateAddonAmount`
   helpers already exist in `BookingServiceImpl` — applying them to creation is
   ~20 lines and closes the same hole on the main path. **Highest-value item left.**
2. No logo image in the e-mail or PDF — both use a styled wordmark. Add a hosted
   PNG for the e-mail and an embedded image for the PDF when artwork exists.
3. Date-range and status filters on the staff invoice list (search only today).
4. Odometer and car-condition fields from the BRD invoice table are still not
   captured at hand-over or return.
5. Still no automated tests.

---

## 28. Phase 3 — verification status

| Check | Result |
|---|---|
| TypeScript (`npx tsc --noEmit`) | ✅ **0 errors** |
| Java: every `ServiceImpl` satisfies its interface | ✅ |
| Java: no unresolved method calls | ✅ (one false positive — the checker cannot see `for (AddonRequest addon : …)` loop variables) |
| Java: brace balance | ✅ |
| `./mvnw clean compile` | ⚠️ **not run here** — no JDK 17/Maven in this environment |
| E-mail rendering in real clients | ⚠️ **not verified** — needs a real send |
| End-to-end against MySQL | ⚠️ **not run here** |

---
---

# Phase 4 — UI & booking document polish

**Date:** 29 July 2026

## 29.1 Past dates blocked

**Frontend** — every booking date picker now carries `min`:

| Screen | Field | `min` |
|---|---|---|
| Landing / CustomerHome reservation card | Pick-up date | today |
| Landing / CustomerHome reservation card | Return date | the chosen pick-up date |
| Modify Booking | Start date | today |
| Modify Booking | End date | the chosen start date |

Tying the return picker to the start date means an end-before-start range
cannot even be selected.

**Backend** — `@FutureOrPresent` on `startDate` in both `BookingRequest` and
`UpdateBookingRequest`, plus a shared `validateBookingDates(...)` in
`BookingServiceImpl` called by **create, guest-create and modify**. The
annotation alone would not cover the guest path, which nests the booking inside
`GuestBookingRequest`.

## 29.2 Booking e-mail and PDF rebranded

Both were still the original plain versions — a text e-mail and a bare PDF that
even printed `Car ID: null`.

**`EmailTemplates.bookingEmail(...)`** now produces the same HTML design as the
invoice e-mail: branded header, greeting, amount banner with a status pill,
booking and vehicle cards, price breakdown with each add-on, a "What happens
next" block and the company footer.

**`generateBookingPdf(...)`** rewritten to match the invoice: branded header,
confirmation banner, billed-to block with the structured address, rental period,
vehicle reserved, itemised charges table and a four-step "What happens next".

The `Car ID: null` row is gone — a booking reserves a **category**, and the
actual vehicle is allocated at the desk, so the PDF now shows the category,
class, daily rate and "At pick-up".

**Shared branding.** Three helpers — `addBrandHeader`, `addBrandFooter`,
`addChargeHeader` — are now used by *both* PDFs, so they cannot drift apart.
The invoice was refactored onto them rather than duplicating the styling.

## 29.3 Invoice Terms & Conditions trimmed

Seven clauses down to **four** — fuel, fines/tolls, uninsured damage, and the
7-day dispute window. The rate-card, late-return and GST clauses were dropped as
they are already evident from the charges table.

## 29.4 Address fields

Already completed in Phase 3 (§19): `addressLine1`, `addressLine2`, city and
state are separate columns, separate form fields on both the profile and the
guest booking form, and are formatted through the single `formatAddress()`
helper. The booking PDF now uses the same structured assembly.

## 29.5 Files changed

**Backend:** `service/impl/PdfServiceImpl.java` (booking PDF rewritten, shared
helpers, shorter T&Cs) · `service/impl/EmailTemplates.java` (+`bookingEmail`) ·
`service/impl/EmailServiceImpl.java` (HTML booking body) ·
`service/impl/BookingServiceImpl.java` (`validateBookingDates`) ·
`dto/request/BookingRequest.java`, `dto/request/UpdateBookingRequest.java`
(`@FutureOrPresent`)

**Frontend:** `screens/Landing.tsx` · `screens/CustomerHome.tsx` ·
`screens/CustomerDashboard.tsx` (all: `TODAY` constant + `min` on date inputs)

## 29.6 Testing checklist

- [ ] Yesterday cannot be selected as a pick-up date on the landing page, home page or Modify Booking
- [ ] The return picker will not allow a date before the chosen pick-up date
- [ ] Posting a past `startDate` directly to `/api/bookings` returns 400 with a clear message
- [ ] Same for `/api/bookings/guest` and `PUT /api/bookings/{id}`
- [ ] Booking confirmation e-mail arrives as styled HTML, matching the invoice e-mail
- [ ] Booking PDF shows the branded header, category (not `Car ID: null`), charges and next steps
- [ ] Booking PDF and invoice PDF look like the same document family
- [ ] Invoice PDF shows exactly four terms

---
---

# Phase 5 — Pick-up vs drop-off hub workflow

**Date:** 29 July 2026

## 30.1 The problem

Every staff query filtered on `pickup_hub_id`. For a one-way rental —
collected at BOM Hub, returned to Nagpur Hub — that meant:

- Nagpur staff **never saw the booking** in their Return module.
- BOM staff saw it in *both* Hand-over and Return.
- Worse, `returnVehicle` validated the **car's** hub against the staff hub.
  The car still belongs to BOM at that moment, so **the return was impossible
  to complete at Nagpur at all** — it always threw *"Vehicle does not belong
  to your hub."*

## 30.2 The rule now

```
Booking: BOM Hub  ──────────────▶  Nagpur Hub
         pickup_hub_id            dropoff_hub_id

BOM staff      Hand-over module ✓        Return module ✗
Nagpur staff   Hand-over module ✗        Return module ✓
Both           Bookings list ✓ (either end)   Invoices ✓ (either end)
```

New `HubScope` enum — `PICKUP`, `RETURN`, `ALL` — passed to
`GET /api/staff/bookings?scope=`.

| Screen | Call |
|---|---|
| Hand-over | `?status=PENDING&scope=PICKUP` |
| Returns | `?status=CONFIRMED&scope=RETURN` |
| Bookings list | no scope → `ALL` (either end) |

## 30.3 Enforcement, not just filtering

The screens filter, but the rules are enforced in the service so a direct API
call cannot bypass them:

| Operation | Check |
|---|---|
| `assignVehicle` | staff hub must equal `booking.pickupHubId` |
| `confirmHandover` | staff hub must equal `booking.pickupHubId` |
| `returnVehicle` | staff hub must equal `booking.dropoffHubId` |

## 30.4 The vehicle moves hubs on return

A one-way rental physically relocates the car. `returnVehicle` now reassigns
`car.hub` to the drop-off hub and logs the move. Without this the vehicle would
stay listed at its original hub forever and could never be hired out from where
it actually is.

## 30.5 Dashboard counts

| Stat | Scope |
|---|---|
| Total | either end |
| Awaiting hand-over (PENDING) | **pick-up** hub |
| On rent / due back (CONFIRMED) | **drop-off** hub |
| Completed, Cancelled | either end |

## 30.6 Invoices

An invoice spans the whole rental, so both hubs may open it: `searchByHub` and
the ownership check now match `pickupHubId` **OR** `dropoffHubId`.

## 30.7 Files changed

**Backend:** `enums/HubScope.java` *(new)* ·
`repository/BookingHeaderRepository.java` (drop-off finders, `findByEitherHub`,
`countByEitherHub`) · `repository/InvoiceHeaderRepository.java` ·
`service/BookingService.java` + `impl` (scope param, scoped stats) ·
`service/impl/HandoverServiceImpl.java` (hub rules, car relocation) ·
`service/impl/InvoiceServiceImpl.java` · `controller/StaffBookingController.java`

**Frontend:** `api/types.ts` (`HubScope`) · `api/index.ts` ·
`screens/staff/Operations.tsx`

## 30.8 Testing checklist

Create a booking **BOM → Nagpur**, then:

- [ ] BOM staff see it under Hand-over; Nagpur staff do **not**
- [ ] Nagpur staff do not see it under Returns until it is handed over
- [ ] BOM staff hand it over successfully
- [ ] After hand-over, **Nagpur** staff see it under Returns; BOM staff do not
- [ ] Nagpur staff complete the return — this previously failed outright
- [ ] The car now appears in **Nagpur's** Vehicles list as AVAILABLE
- [ ] Both hubs can find the invoice in their Invoices screen
- [ ] Nagpur calling `assign-vehicle` for this booking returns 403
- [ ] BOM calling `return` for this booking returns 403
- [ ] A same-hub booking (BOM → BOM) still works end to end
- [ ] Dashboard: PENDING counts at the pick-up hub, CONFIRMED at the drop-off hub

---

# 31. Phase 6 — booking address & the "click twice" profile link

Two problems reported after Phase 5 testing.

## 31.1 The address was collected but thrown away

The "Your Info" step showed **Address Line 1** and **Address Line 2** to
everybody, but **State, City and Pincode only to guests**. Worse, for a
signed-in customer none of the four fields were ever sent anywhere:

```ts
// BookingRequestPayload had no address fields at all
const bookingPayload: BookingRequestPayload = {
  carTypeId: Number(vehicle.id),
  startDate: ...,
  // ...amounts and hubs only
}
```

The chain that feeds the invoice is:

```
Your Info form → Customer record → booking snapshot → invoice → PDF / e-mail
```

The very first link was missing, so unless a customer had visited **My
Information** and filled the address in by hand, every booking snapshot — and
therefore every invoice — carried a blank address.

The licence and passport numbers had the same hole: `BookingRequest` already
had `drivingLicenseNo` / `passportNo` and `saveDocumentsOnProfile()` already
read them, but the browser never sent them.

## 31.2 What changed

**Backend**

`BookingRequest` gained five optional fields: `addressLine1`, `addressLine2`,
`stateId`, `cityId`, `pincode`.

`BookingServiceImpl.saveDocumentsOnProfile()` now also writes the address onto
the `Customer` record, using the same rule the documents already used —
**a blank value is ignored, never written**. Writing blanks would wipe a good
address every time a form posted an empty field.

State and city are looked up through their repositories rather than trusted as
raw ids, and an id that does not resolve is skipped: a bad address must never
stop somebody renting a car.

This runs *before* the booking snapshot is taken, because the snapshot reads
straight off the `Customer`. Order matters here.

**Frontend**

- Address moved out of the Personal Information card into its own **Billing
  Address** card, shown to guests and signed-in customers alike.
- State / City / Pincode are no longer guest-only.
- A signed-in customer's address is pre-filled from `GET /customer/profile`
  and anything they change is saved back.
- Required markers: **Address Line 1 \*, State \*, City \*** — Line 2 and
  Pincode are optional, and Pincode is validated as 6 digits only when filled.
- The confirm screen prints the address back before the customer commits,
  since that is what will appear on the invoice.
- The payload now carries `drivingLicenseNo`, `passportNo` and all five
  address fields.

One deliberate subtlety: the "cities depend on state" effect does **not**
clear the selected city. On first load the pre-filled state and city arrive
together, and clearing on every state change would wipe the saved city. The
state picker clears it explicitly instead, so it only fires on a real edit.

## 31.3 "My Profile" needed two clicks

`NavFn` was `(screen: Screen) => void` — a screen name and nothing else. The
customer dashboard is really six tabs behind one screen name, and its tab
state always started at `'dashboard'`:

```tsx
const [activeTab, setActiveTab] = useState('dashboard')
```

So **My Profile** in the header could only drop the customer on the dashboard
tab; they then had to click **My Information** in the sidebar to actually
reach their profile. Two clicks for one destination.

On the public landing page it was worse — both menu items called
`nav(dashNav(role))`, which returns `'customer-home'`. "My Profile" opened the
home page and never showed a profile at all.

**Fix.** `NavFn` takes an optional tab:

```ts
export type NavFn = (screen: Screen, tab?: string) => void
```

There is no router, so `App.nav()` parks the requested tab in
`sessionStorage` and the dashboard reads it once as it mounts, then clears it
— otherwise every later visit would reopen the same tab. The value is checked
against `navItems` before use.

The header menus now point at real destinations: Dashboard, My Profile
(`my-info`) and My Bookings (`my-bookings`).

Also fixed while in there: after confirming a booking a signed-in customer was
sent to the **public** landing page, which looks exactly like being logged
out. They now go to `customer-home`; guests still go to `landing`.

## 31.4 Files changed

**Backend:** `dto/request/BookingRequest.java` ·
`service/impl/BookingServiceImpl.java`

**Frontend:** `data.ts` · `App.tsx` · `api/types.ts` ·
`screens/BookingFlow.tsx` · `screens/CustomerDashboard.tsx` ·
`screens/CustomerHome.tsx` · `screens/Landing.tsx`

## 31.5 Testing checklist

- [ ] **Guest booking:** address card shows, Line 1 / State / City are
      required, a 5-digit pincode is rejected, a 6-digit one accepted
- [ ] **Signed-in booking:** address arrives pre-filled from the profile
- [ ] Change the state — the city clears and reloads for the new state
- [ ] Reload the Your Info step — the saved city is still selected
- [ ] Confirm screen shows the address back, in words, before committing
- [ ] After booking, **My Information** shows the address that was typed
- [ ] Leaving an address field blank does **not** wipe the saved one
- [ ] The invoice PDF and the e-mail both print the address
- [ ] Licence number typed at booking time appears on the profile afterwards
- [ ] Header **My Profile** opens My Information in **one** click, from the
      landing page, the customer home page and the mobile menu
- [ ] After confirming a booking, a signed-in customer lands on customer home,
      a guest on the public landing page
