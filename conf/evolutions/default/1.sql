# --- !Ups

create table area (
  id                        varchar(255) not null,
  name                      varchar(255),
  constraint pk_area primary key (id))
;

create table deer_lice (
  id                        integer auto_increment not null,
  sum                       varchar(255),
  constraint pk_deer_lice primary key (id))
;

create table elk (
  id                        bigint auto_increment not null,
  date                      date,
  weigth                    integer,
  age                       double,
  antlers                   integer,
  veal                      integer,
  twin                      integer,
  sum_tick_id               integer,
  sum_lice_id               integer,
  sex_id                    integer,
  area_id                   varchar(255),
  huntingfield_id           varchar(255),
  constraint pk_elk primary key (id))
;

create table hunting_field (
  id                        varchar(255) not null,
  leader                    varchar(255),
  name                      varchar(255),
  constraint pk_hunting_field primary key (id))
;

create table sex (
  id                        integer auto_increment not null,
  type                      varchar(255),
  constraint pk_sex primary key (id))
;

create table tick (
  id                        integer auto_increment not null,
  sum                       varchar(255),
  constraint pk_tick primary key (id))
;

alter table elk add constraint fk_elk_sumTick_1 foreign key (sum_tick_id) references tick (id) on delete restrict on update restrict;
create index ix_elk_sumTick_1 on elk (sum_tick_id);
alter table elk add constraint fk_elk_sumLice_2 foreign key (sum_lice_id) references deer_lice (id) on delete restrict on update restrict;
create index ix_elk_sumLice_2 on elk (sum_lice_id);
alter table elk add constraint fk_elk_sex_3 foreign key (sex_id) references sex (id) on delete restrict on update restrict;
create index ix_elk_sex_3 on elk (sex_id);
alter table elk add constraint fk_elk_area_4 foreign key (area_id) references area (id) on delete restrict on update restrict;
create index ix_elk_area_4 on elk (area_id);
alter table elk add constraint fk_elk_huntingfield_5 foreign key (huntingfield_id) references hunting_field (id) on delete restrict on update restrict;
create index ix_elk_huntingfield_5 on elk (huntingfield_id);



# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table area;

drop table deer_lice;

drop table elk;

drop table hunting_field;

drop table sex;

drop table tick;

SET FOREIGN_KEY_CHECKS=1;

