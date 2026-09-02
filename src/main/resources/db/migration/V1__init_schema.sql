CREATE TABLE applicants (
                            id           VARCHAR(36) PRIMARY KEY,
                            oidc_subject VARCHAR(255) NOT NULL,
                            email        VARCHAR(255) NOT NULL,
                            first_name   VARCHAR(100) NOT NULL,
                            last_name    VARCHAR(100) NOT NULL,
                            created_at   TIMESTAMPTZ NOT NULL,

                            CONSTRAINT uk_applicant_oidc_subject UNIQUE (oidc_subject),
                            CONSTRAINT uk_applicant_email UNIQUE (email)
);

CREATE TABLE scholarships (
                              id             VARCHAR(36) PRIMARY KEY,
                              name           VARCHAR(255) NOT NULL,
                              organization   VARCHAR(255) NOT NULL,
                              description    TEXT,
                              official_url   VARCHAR(500),
                              opens_at       DATE,
                              deadline       DATE NOT NULL,
                              status         VARCHAR(20) NOT NULL DEFAULT 'OPEN',
                              created_at     TIMESTAMPTZ NOT NULL,

                              CONSTRAINT chk_scholarship_status
                                  CHECK (status IN ('OPEN', 'CLOSED', 'ARCHIVED'))
);

CREATE INDEX idx_scholarships_deadline ON scholarships (deadline);
CREATE INDEX idx_scholarships_status ON scholarships (status);

CREATE TABLE applications (
                              id             VARCHAR(36) PRIMARY KEY,
                              applicant_id   VARCHAR(36) NOT NULL REFERENCES applicants (id) ON DELETE CASCADE,
                              scholarship_id VARCHAR(36) NOT NULL REFERENCES scholarships (id) ON DELETE RESTRICT,
                              status         VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
                              submitted_at   TIMESTAMPTZ,
                              notes          TEXT,
                              created_at     TIMESTAMPTZ NOT NULL,
                              updated_at     TIMESTAMPTZ NOT NULL,

                              CONSTRAINT uk_application_applicant_scholarship UNIQUE (applicant_id, scholarship_id),
                              CONSTRAINT chk_application_status
                                  CHECK (status IN ('DRAFT', 'SUBMITTED', 'UNDER_REVIEW', 'ACCEPTED', 'REJECTED'))
);

CREATE INDEX idx_applications_applicant_id ON applications (applicant_id);
CREATE INDEX idx_applications_status ON applications (status);

CREATE TABLE documents (
                           id             VARCHAR(36) PRIMARY KEY,
                           application_id VARCHAR(36) NOT NULL REFERENCES applications (id) ON DELETE CASCADE,
                           type           VARCHAR(30) NOT NULL,
                           s3_url         VARCHAR(1000) NOT NULL,
                           file_name      VARCHAR(255) NOT NULL,
                           uploaded_at    TIMESTAMPTZ NOT NULL,

                           CONSTRAINT uk_document_application_type UNIQUE (application_id, type),
                           CONSTRAINT chk_document_type
                               CHECK (type IN ('CV', 'MOTIVATION_LETTER', 'TRANSCRIPT', 'RECOMMENDATION_LETTER'))
);

CREATE INDEX idx_documents_application_id ON documents (application_id);