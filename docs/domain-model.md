# Domain Model Documentation

## Core Domains and Relationships

### 1. Contract Domain
- **Core Entity**: Contract
  - ContractID (identifier, UUIDv7-based)
  - ProprietorID (reference to contractor)
  - ContractNo (business identifier)
  - ContractPeriod (start/end dates)
  - ContractStatus (DRAFT/ACTIVE/TERMINATED/VOIDED/EXPIRED/RENEWED)
  - BillingConditions:
    * TimeBasedCondition (hourlyRate)
    * FixedRateCondition (fixedAmount)
  - Note (optional)
  - Version (for tracking changes)
- **Business Rules**:
  - Contract status transitions must follow defined flow
  - Contract period must be valid (start before end)
  - Contract number must be unique
  - Version increments with each update
  - Billing conditions must be valid (non-negative rates/amounts)

### 2. Attendance Domain
- **Core Entity**: Attendance
  - AttendanceID (identifier, UUIDv7-based)
  - ContractID (reference to contract)
  - AttendanceDate (date of work)
  - AttendanceEvents:
    * TimeRecordingEvent (initial time record)
      - AttendanceEventID (identifier, UUIDv7-based)
      - RecordAt (timestamp)
    * TimeCorrectionEvent (corrections to records)
      - AttendanceEventID (identifier, UUIDv7-based)
      - CorrectDateTime (timestamp)
      - CorrectTargetEventID (reference to original event)
- **Business Rules**:
  - Time records must come in pairs (in/out)
  - Corrections must reference existing time records
  - Total hours calculated from effective records only
  - Incomplete records (odd number) result in 0 hours
  - Records must be associated with active contract
- **Required for**:
  - Time-based billing calculations
  - Work verification
  - Compliance tracking

### 3. Invoice Domain
- **Core Entity**: Invoice
  - InvoiceID (identifier, UUIDv7-based)
  - ContractID (reference to contract)
  - BillingPeriod (period being billed)
  - Items:
    * For TimeBasedCondition:
      - Total hours from Attendance records
      - Rate from Contract
      - Subtotal calculation
    * For FixedRateCondition:
      - Fixed amount from Contract
  - Status (DRAFT/ISSUED/PAID)
- **Business Rules**:
  - Only DRAFT invoices can be calculated
  - Status transitions: DRAFT -> ISSUED -> PAID
  - Calculations based on contract billing conditions
  - Time-based billing requires attendance records
  - Fixed-rate billing uses contract amount directly
- **Required for**:
  - Monthly billing
  - Payment tracking
  - Financial reporting

## Domain Relationships and Workflows

### Contract-Attendance Relationship
- Contract must exist and be active for attendance recording
- Attendance records linked to contract for billing
- Contract billing conditions determine attendance usage

### Attendance-Invoice Relationship
- Attendance records grouped by billing period
- Total hours calculated from valid record pairs
- Corrections affect invoice calculations
- Incomplete records excluded from billing

### Contract-Invoice Relationship
- Contract defines billing conditions
- Invoice items mapped to billing conditions
- Contract status affects invoice generation
- Multiple invoices possible per contract

### Key Workflows

1. Time Recording Workflow:
   - Verify active contract
   - Record time event
   - Pair with previous record
   - Calculate total hours

2. Invoice Generation Workflow:
   - Create draft invoice for contract
   - Collect attendance records for period
   - Apply billing conditions
   - Calculate total amount
   - Issue invoice

3. Payment Processing Workflow:
   - Verify invoice is issued
   - Record payment
   - Update invoice status

## Implementation Status and Challenges

### Current Implementation Status
1. Core Domain Models ✓
   - Contract domain model
   - Attendance domain model
   - Invoice domain model

2. ID Implementations ✓
   - All domains using UUIDv7-based IDs
   - ID generation tested and working
   - Value equality implemented

3. Business Logic Implementation ✓
   - Contract management
   - Attendance recording
   - Invoice calculation
   - Status management

### Architectural Challenges

#### 1. Circular Dependencies
Current implementation has circular dependencies between domains:
- Contract depends on Attendance for billing calculations
- Attendance depends on Contract for validation
- Invoice depends on both Contract and Attendance

This creates:
- Build issues
- Testing difficulties
- Tight coupling between domains

#### 2. Proposed Architectural Improvements

1. Shared Kernel
   - Move common interfaces to shared module:
     * BillingCalculator
     * TimeCalculator
     * StatusTransition
   - Define domain events for cross-domain communication

2. Domain Boundaries
   - Contract Domain:
     * Remove direct Attendance dependency
     * Use interfaces for billing calculations
     * Emit events for status changes
   - Attendance Domain:
     * Remove direct Contract dependency
     * Use interfaces for contract validation
     * Subscribe to contract events
   - Invoice Domain:
     * Use interfaces for calculations
     * Subscribe to relevant domain events

3. Implementation Approach
   - Create shared interfaces in shared module
   - Implement interfaces in respective domains
   - Use dependency injection for cross-domain services
   - Implement event-driven communication

### Next Steps

1. Architectural Refactoring:
   - Create shared interfaces
   - Restructure domain dependencies
   - Implement event system
   - Update build configuration

2. Testing Strategy:
   - Unit tests per domain
   - Integration tests for workflows
   - Event handling tests
   - End-to-end scenario tests

3. Documentation:
   - Update technical documentation
   - Document event flows
   - Add architecture diagrams
   - Update API documentation
