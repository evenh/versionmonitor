create sequence hibernate_sequence;
create sequence project_id_seq;


create table if not exists project (
  id          bigserial    not null constraint project_pkey primary key,
  description varchar(255),
  identifier  varchar(255) not null,
  name        varchar(255) not null
);

create table if not exists releases (
  id          bigint       not null constraint releases_pkey primary key,
  released_at timestamp    not null,
  url         varchar(255) not null,
  version     varchar(255) not null,
  project_id  bigint
    constraint fkkc96tfkfbo7h16txn8y7hn87 references project
);

create table if not exists subscription (
  id         bigint       not null constraint subscription_pkey primary key,
  identifier varchar(255) not null,
  name       varchar(255) not null
);

create table if not exists project_subscriptions (
  project_id       bigint not null
    constraint fkimadi3cn36jwb6i8xfg49v0se references project,
  subscriptions_id bigint not null
    constraint fkk11tlb7u4idjxawbiuq6hacjk references subscription,
  constraint project_subscriptions_pkey
  primary key (project_id, subscriptions_id)
);

create table if not exists subscription_slack (
  channel varchar(255),
  id      bigint not null constraint subscription_slack_pkey primary key
  constraint fkkuhfwhbsxxe3cm0r555a3t4bl references subscription
);

create table if not exists project_github (
  id bigint not null constraint project_github_pkey primary key
  constraint fkcxopdmx2c654hotr912p3n3np references project
);

create table if not exists project_npm (
  id bigint not null constraint project_npm_pkey primary key
  constraint fkrgqf8x9grcsffer5e7c6bgh2p references project
);
