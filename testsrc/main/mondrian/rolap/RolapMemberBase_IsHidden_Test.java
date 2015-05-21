/*
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// You must accept the terms of that agreement to use this software.
//
// Copyright (C) 2015-2015 Pentaho and others
// All Rights Reserved.
*/
package mondrian.rolap;

import mondrian.olap.Dimension;
import mondrian.olap.Member;
import mondrian.olap.Property;
import mondrian.olap.Schema;
import mondrian.olap.SchemaReader;

import junit.framework.TestCase;

import java.util.Collections;
import java.util.List;

import static mondrian.olap.Member.MemberType.REGULAR;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Andrey Khayrutdinov
 */
public class RolapMemberBase_IsHidden_Test extends TestCase {

  private SchemaReader schemaReader;
  private RolapLevel level;
  private RolapMemberBase member;
  private RolapMemberBase parent;

  public void setUp() throws Exception {
    schemaReader = mock(SchemaReader.class);

    Schema schema = mock(Schema.class);
    when(schema.getSchemaReader()).thenReturn(schemaReader);

    Dimension dimension = mock(Dimension.class);
    when(dimension.getSchema()).thenReturn(schema);

    RolapHierarchy hierarchy = mock(RolapHierarchy.class);
    when(hierarchy.getDimension()).thenReturn(dimension);

    level = mock(RolapLevel.class);
    when(level.getHierarchy()).thenReturn(hierarchy);

    parent = mock(RolapMemberBase.class);

    member = new RolapMemberBase(parent, level, "key", "test", REGULAR);
    when(member.getDimension()).thenReturn(dimension);
  }


  public void testHideNever() throws Exception {
    setHideCondition(RolapLevel.HideMemberCondition.Never);

    assertFalse(member.isHidden());
  }


  public void testHideIfBlankName_NotBlank() throws Exception {
    setHideCondition(RolapLevel.HideMemberCondition.IfBlankName);
    setMemberName("name");

    assertFalse(member.isHidden());
  }

  public void testHideIfBlankName_Blank_NoChildren() throws Exception {
    setHideCondition(RolapLevel.HideMemberCondition.IfBlankName);
    setMemberName("");

    setMemberChildren(Collections.<Member>emptyList());

    assertTrue(member.isHidden());
  }

  public void testHideIfBlankName_Blank_HiddenChildren() throws Exception {
    setHideCondition(RolapLevel.HideMemberCondition.IfBlankName);
    setMemberName("");

    Member child = mockMember(true);
    setMemberChildren(Collections.singletonList(child));

    assertTrue(member.isHidden());
  }

  public void testHideIfBlankName_Blank_NonHiddenChildren() throws Exception {
    setHideCondition(RolapLevel.HideMemberCondition.IfBlankName);
    setMemberName("");

    Member child = mockMember(false);
    setMemberChildren(Collections.singletonList(child));

    assertFalse(member.isHidden());
  }


  public void testHideIfParentName_NotSame() throws Exception {
    setHideCondition(RolapLevel.HideMemberCondition.IfParentsName);
    setMemberName("name");

    when(parent.getName()).thenReturn("parent's name");

    assertFalse(member.isHidden());
  }

  public void testHideIfParentName_Same_NoChildren() throws Exception {
    setHideCondition(RolapLevel.HideMemberCondition.IfParentsName);
    setMemberName("name");

    when(parent.getName()).thenReturn("name");
    setMemberChildren(Collections.<Member>emptyList());

    assertTrue(member.isHidden());
  }

  public void testHideIfParentName_Same_HiddenChildren() throws Exception {
    setHideCondition(RolapLevel.HideMemberCondition.IfParentsName);
    setMemberName("name");

    when(parent.getName()).thenReturn("name");

    Member child = mockMember(true);
    setMemberChildren(Collections.singletonList(child));

    assertTrue(member.isHidden());
  }

  public void testHideIfParentName_Same_NonHiddenChildren() throws Exception {
    setHideCondition(RolapLevel.HideMemberCondition.IfParentsName);
    setMemberName("name");

    when(parent.getName()).thenReturn("name");

    Member child = mockMember(false);
    setMemberChildren(Collections.singletonList(child));

    assertFalse(member.isHidden());
  }


  private void setHideCondition(RolapLevel.HideMemberCondition condition) {
    when(level.getHideMemberCondition()).thenReturn(condition);
  }

  private void setMemberName(String name) {
    member.setProperty(Property.NAME.name, name);
  }

  private void setMemberChildren(List<Member> children) {
    when(schemaReader.getMemberChildren(eq(member))).thenReturn(children);
  }

  private Member mockMember(boolean hidden) {
    Member child = mock(Member.class);
    when(child.isHidden()).thenReturn(hidden);
    return child;
  }
}

// End RolapMemberBase_IsHidden_Test.java
