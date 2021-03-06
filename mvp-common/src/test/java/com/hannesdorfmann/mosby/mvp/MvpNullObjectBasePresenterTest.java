/*
 *  Copyright 2015. Hannes Dorfmann.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.hannesdorfmann.mosby.mvp;

import android.support.annotation.NonNull;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Hannes Dorfmann
 */
public class MvpNullObjectBasePresenterTest {

  public interface TestView extends MvpView {
    public void showFoo(TestData data);

    public void showThat();
  }

  /**
   * Just a stupid interface to check if the right interface will be picked
   */
  public interface FooInterface {

    public void foo();
  }

  /**
   * Just a stupid interface to check if the right interface will be picked
   */
  public interface BarInterface {
    public void bar();
  }

  public interface OtherTestView extends MvpView {
    public void showOtherMvpView();
  }

  public interface SubMvpView extends TestView {
  }

  static class SubMvpViewPresenter extends MvpNullObjectBasePresenter<SubMvpView> {

    public void invokeShowThat() {
      getView().showThat();
    }
  }

  public static class TestData {
  }

  public static class ViewWithMulitpleInterfaces
      implements FooInterface, BarInterface, OtherTestView, TestView {
    @Override public void bar() {
    }

    @Override public void foo() {
    }

    @Override public void showFoo(TestData data) {
    }

    @Override public void showThat() {
    }

    @Override public void showOtherMvpView() {
    }
  }

  public static class TestNullObjectPresenter
      extends MvpNullObjectBasePresenter<MvpNullObjectBasePresenterTest.TestView> {

    public void viewShowFoo(TestData data) {
      getView().showFoo(data);
    }

    public void viewShowThat() {
      getView().showThat();
    }

    @NonNull @Override public TestView getView() {
      return super.getView();
    }
  }

  public static class UselessGenericParamsPresenter<M, I>
      extends MvpNullObjectBasePresenter<TestView> {

    void viewShowThat() {
      getView().showThat();
    }
  }

  public static class ParameterlessConstructor<M> extends TestNullObjectPresenter {
  }

  public static class SubclassConstructor extends ParameterlessConstructor<TestData> {
  }

  @Test public void testUselessGenericsParamsPresenter() {
    // no exception should be thrown

    TestView view = new TestView() {
      @Override public void showFoo(TestData data) {
      }

      @Override public void showThat() {
      }
    };
    UselessGenericParamsPresenter presenter =
        new UselessGenericParamsPresenter<TestData, FooInterface>();
    pickingCorrectViewInterface(presenter);
    testAttachDetach(presenter, view);

    presenter.attachView(view);
    presenter.viewShowThat();
    presenter.detachView(false);
    presenter.viewShowThat();
  }


  @Test public void testConstructorGenericParameterless() {
    // no exception should be thrown

    TestView view = new TestView() {
      @Override public void showFoo(TestData data) {
      }

      @Override public void showThat() {
      }
    };
    ParameterlessConstructor<TestData> presenter = new ParameterlessConstructor<TestData>();
    pickingCorrectViewInterface(presenter);
    testAttachDetach(presenter, view);

    presenter.attachView(view);
    presenter.viewShowThat();
    presenter.detachView(false);
    presenter.viewShowThat();
  }

  @Test public void testConstructorDirectlyBaseClass() {
    // no exception should be thrown

    TestView view = new TestView() {
      @Override public void showFoo(TestData data) {
      }

      @Override public void showThat() {
      }
    };

    MvpNullObjectBasePresenter presenter = new MvpNullObjectBasePresenter<TestView>() {
    };
    pickingCorrectViewInterface(presenter);
    testAttachDetach(presenter, view);
  }

  @Test public void testConstructorSubClass() {
    // no exception should be thrown
    SubclassConstructor presenter = new SubclassConstructor();

    TestView view = new TestView() {
      @Override public void showFoo(TestData data) {
      }

      @Override public void showThat() {
      }
    };

    pickingCorrectViewInterface(presenter);
    testAttachDetach(presenter, view);

    presenter.attachView(view);
    presenter.viewShowThat();
    presenter.detachView(false);
    presenter.viewShowThat();
  }

  @Test public void testSubviewInterface() {
    // no exception should be thrown

    SubMvpView view = new SubMvpView() {
      @Override public void showFoo(TestData data) {

      }

      @Override public void showThat() {

      }
    };

    SubMvpViewPresenter presenter = new SubMvpViewPresenter();
    testAttachDetach(presenter, view);
    presenter.attachView(view);
    presenter.invokeShowThat();
    presenter.detachView(false);
    presenter.invokeShowThat();
  }

  private <V extends MvpView> void testAttachDetach(MvpNullObjectBasePresenter<V> presenter,
      V view) {

    Assert.assertNotNull(presenter.getView());

    presenter.attachView(view);
    Assert.assertNotNull(presenter.getView());
    Assert.assertTrue(presenter.getView() == view);

    // Test with retainInstance == false
    presenter.detachView(false);
    Assert.assertNotNull(presenter.getView());
    Assert.assertTrue(presenter.getView() != view); // Null Object view

    // Reattach real view
    presenter.attachView(view);
    Assert.assertNotNull(presenter.getView());
    Assert.assertTrue(presenter.getView() == view);

    // Test with retainInstance == true
    presenter.detachView(true);
    Assert.assertNotNull(presenter.getView());
    Assert.assertTrue(presenter.getView() != view); // Null Object view
  }

  private void pickingCorrectViewInterface(MvpNullObjectBasePresenter<TestView> presenter) {

    ViewWithMulitpleInterfaces view = new ViewWithMulitpleInterfaces();

    presenter.attachView(view);
    Assert.assertNotNull(presenter.getView());
    Assert.assertTrue(view == presenter.getView());

    presenter.detachView(false);
    Assert.assertNotNull(presenter.getView());
    Assert.assertFalse(presenter.getView() == view);

    // Invoke methods on null object
    presenter.getView().showFoo(new TestData());
    presenter.getView().showThat();
  }
}
