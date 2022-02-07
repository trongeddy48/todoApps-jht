import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './todo.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const TodoDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const todoEntity = useAppSelector(state => state.todo.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="todoDetailsHeading">
          <Translate contentKey="todoAppsApp.todo.detail.title">Todo</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{todoEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="todoAppsApp.todo.name">Name</Translate>
            </span>
          </dt>
          <dd>{todoEntity.name}</dd>
          <dt>
            <span id="description">
              <Translate contentKey="todoAppsApp.todo.description">Description</Translate>
            </span>
          </dt>
          <dd>{todoEntity.description}</dd>
          <dt>
            <span id="createdAt">
              <Translate contentKey="todoAppsApp.todo.createdAt">Created At</Translate>
            </span>
          </dt>
          <dd>{todoEntity.createdAt ? <TextFormat value={todoEntity.createdAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="updatedAt">
              <Translate contentKey="todoAppsApp.todo.updatedAt">Updated At</Translate>
            </span>
          </dt>
          <dd>{todoEntity.updatedAt ? <TextFormat value={todoEntity.updatedAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
        </dl>
        <Button tag={Link} to="/todo" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/todo/${todoEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default TodoDetail;
