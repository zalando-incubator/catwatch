--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: contributor; Type: TABLE; Schema: public; Owner: cat1; Tablespace: 
--

CREATE TABLE contributor (
    id bigint NOT NULL,
    organization_id bigint NOT NULL,
    snapshot_date timestamp without time zone NOT NULL,
    name character varying(255),
    organization_name character varying(255),
    organizational_commits_count integer,
    organizational_projects_count integer,
    personal_commits_count integer,
    personal_projects_count integer,
    url character varying(255)
);


ALTER TABLE contributor OWNER TO cat1;

--
-- Name: language_list; Type: TABLE; Schema: public; Owner: cat1; Tablespace: 
--

CREATE TABLE language_list (
    project_id integer NOT NULL,
    language character varying(255)
);


ALTER TABLE language_list OWNER TO cat1;

--
-- Name: project; Type: TABLE; Schema: public; Owner: cat1; Tablespace: 
--

CREATE TABLE project (
    id integer NOT NULL,
    commits_count integer,
    contributors_count integer,
    description character varying(255),
    forks_count integer,
    git_hub_project_id bigint,
    last_pushed character varying(255),
    name character varying(255),
    organization_name character varying(255),
    primary_language character varying(255),
    score integer,
    snapshot_date timestamp without time zone,
    stars_count integer,
    url character varying(255)
);


ALTER TABLE project OWNER TO cat1;

--
-- Name: project_id_seq; Type: SEQUENCE; Schema: public; Owner: cat1
--

CREATE SEQUENCE project_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE project_id_seq OWNER TO cat1;

--
-- Name: project_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: cat1
--

ALTER SEQUENCE project_id_seq OWNED BY project.id;


--
-- Name: statistics; Type: TABLE; Schema: public; Owner: cat1; Tablespace: 
--

CREATE TABLE statistics (
    id bigint NOT NULL,
    snapshot_date timestamp without time zone NOT NULL,
    all_contributors_count integer,
    all_forks_count integer,
    all_size_count integer,
    all_stars_count integer,
    members_count integer,
    organization_name character varying(255),
    private_project_count integer,
    program_languages_count integer,
    public_project_count integer,
    tags_count integer,
    teams_count integer
);


ALTER TABLE statistics OWNER TO cat1;

--
-- Name: id; Type: DEFAULT; Schema: public; Owner: cat1
--

ALTER TABLE ONLY project ALTER COLUMN id SET DEFAULT nextval('project_id_seq'::regclass);


--
-- Name: contributor_pkey; Type: CONSTRAINT; Schema: public; Owner: cat1; Tablespace: 
--

ALTER TABLE ONLY contributor
    ADD CONSTRAINT contributor_pkey PRIMARY KEY (id, organization_id, snapshot_date);


--
-- Name: project_pkey; Type: CONSTRAINT; Schema: public; Owner: cat1; Tablespace: 
--

ALTER TABLE ONLY project
    ADD CONSTRAINT project_pkey PRIMARY KEY (id);


--
-- Name: statistics_pkey; Type: CONSTRAINT; Schema: public; Owner: cat1; Tablespace: 
--

ALTER TABLE ONLY statistics
    ADD CONSTRAINT statistics_pkey PRIMARY KEY (id, snapshot_date);


--
-- Name: fk_4ep3aidpq8d8kepexjt21sn5b; Type: FK CONSTRAINT; Schema: public; Owner: cat1
--

ALTER TABLE ONLY language_list
    ADD CONSTRAINT fk_4ep3aidpq8d8kepexjt21sn5b FOREIGN KEY (project_id) REFERENCES project(id);


--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--

